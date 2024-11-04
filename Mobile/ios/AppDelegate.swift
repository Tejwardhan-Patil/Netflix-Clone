import UIKit
import UserNotifications
import BackgroundTasks

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate, UNUserNotificationCenterDelegate {

    var window: UIWindow?

    // MARK: - Application Life Cycle

    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Initialize Window
        window = UIWindow(frame: UIScreen.main.bounds)
        let mainVC = MainViewController() // Main entry point
        let navigationController = UINavigationController(rootViewController: mainVC)
        window?.rootViewController = navigationController
        window?.makeKeyAndVisible()

        // Configure Push Notifications
        configurePushNotifications(application: application)

        // Register background tasks
        registerBackgroundTasks()

        // Handle Deep Linking (for Universal Links and Custom URLs)
        handleLaunchOptions(launchOptions)

        return true
    }

    // MARK: - Scene Life Cycle (iOS 13+)

    func application(_ application: UIApplication,
                     configurationForConnecting connectingSceneSession: UISceneSession,
                     options: UIScene.ConnectionOptions) -> UISceneConfiguration {
        return UISceneConfiguration(name: "Default Configuration", sessionRole: connectingSceneSession.role)
    }

    func application(_ application: UIApplication, didDiscardSceneSessions sceneSessions: Set<UISceneSession>) {
        // Handle scene sessions being discarded
    }

    // MARK: - Push Notifications Configuration

    private func configurePushNotifications(application: UIApplication) {
        let center = UNUserNotificationCenter.current()
        center.delegate = self
        center.requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
            if granted {
                DispatchQueue.main.async {
                    application.registerForRemoteNotifications()
                }
            }
        }
    }

    // MARK: - UNUserNotificationCenterDelegate

    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        let tokenParts = deviceToken.map { String(format: "%02.2hhx", $0) }
        let token = tokenParts.joined()
        print("Device Token: \(token)")
        // Send device token to server
    }

    func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
        print("Failed to register: \(error)")
    }

    // Handle notification received in the foreground
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                willPresent notification: UNNotification,
                                withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        completionHandler([.alert, .sound, .badge])
    }

    // Handle notification tap action
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                didReceive response: UNNotificationResponse,
                                withCompletionHandler completionHandler: @escaping () -> Void) {
        // Process notification response
        completionHandler()
    }

    // MARK: - Background Task Registration

    private func registerBackgroundTasks() {
        BGTaskScheduler.shared.register(forTaskWithIdentifier: "com.website.app.refresh", using: nil) { task in
            self.handleAppRefresh(task: task as! BGAppRefreshTask)
        }

        BGTaskScheduler.shared.register(forTaskWithIdentifier: "com.website.app.cleanup", using: nil) { task in
            self.handleCleanupTask(task: task as! BGProcessingTask)
        }
    }

    // MARK: - Background Task Handling

    func handleAppRefresh(task: BGAppRefreshTask) {
        scheduleAppRefresh()
        let queue = OperationQueue()
        queue.maxConcurrentOperationCount = 1

        let operation = RefreshOperation()
        queue.addOperation(operation)

        task.expirationHandler = {
            queue.cancelAllOperations()
        }

        operation.completionBlock = {
            task.setTaskCompleted(success: !operation.isCancelled)
        }
    }

    func handleCleanupTask(task: BGProcessingTask) {
        let queue = OperationQueue()
        queue.maxConcurrentOperationCount = 1

        let operation = CleanupOperation()
        queue.addOperation(operation)

        task.expirationHandler = {
            queue.cancelAllOperations()
        }

        operation.completionBlock = {
            task.setTaskCompleted(success: !operation.isCancelled)
        }
    }

    func scheduleAppRefresh() {
        let request = BGAppRefreshTaskRequest(identifier: "com.website.app.refresh")
        request.earliestBeginDate = Date(timeIntervalSinceNow: 15 * 60) // 15 minutes
        do {
            try BGTaskScheduler.shared.submit(request)
        } catch {
            print("Unable to schedule app refresh: \(error)")
        }
    }

    func scheduleCleanupTask() {
        let request = BGProcessingTaskRequest(identifier: "com.website.app.cleanup")
        request.requiresNetworkConnectivity = true // Requires network
        request.requiresExternalPower = false // Doesn't require being plugged in

        do {
            try BGTaskScheduler.shared.submit(request)
        } catch {
            print("Unable to schedule cleanup task: \(error)")
        }
    }

    // MARK: - Handling URL Schemes & Universal Links

    func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey: Any]) -> Bool {
        print("Opened URL: \(url)")
        return true
    }

    func application(_ application: UIApplication,
                     continue userActivity: NSUserActivity,
                     restorationHandler: @escaping ([UIResponder]) -> Void) -> Bool {
        print("Continuing user activity: \(userActivity)")
        return true
    }

    // MARK: - Background Fetch Support

    func application(_ application: UIApplication, performFetchWithCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
        fetchData { newData in
            if newData {
                completionHandler(.newData)
            } else {
                completionHandler(.noData)
            }
        }
    }

    // MARK: - Data Fetch Simulation

    private func fetchData(completion: (Bool) -> Void) {
        completion(true) // Simulate data fetch
    }

    // MARK: - App Termination Handling

    func applicationWillTerminate(_ application: UIApplication) {
        print("App is terminating")
    }

    // MARK: - Handling Launch Options (Deep Linking)

    private func handleLaunchOptions(_ launchOptions: [UIApplication.LaunchOptionsKey: Any]?) {
        if let url = launchOptions?[.url] as? URL {
            print("App launched with URL: \(url)")
        }
        if let activity = launchOptions?[.userActivityDictionary] as? [String: Any] {
            print("App launched with user activity: \(activity)")
        }
    }

    // MARK: - Error Handling

    func handleError(_ error: Error) {
        print("Error occurred: \(error.localizedDescription)")
    }
}

// MARK: - Background Operations

class RefreshOperation: Operation {
    override func main() {
        if isCancelled { return }
        print("Refreshing data in the background")
    }
}

class CleanupOperation: Operation {
    override func main() {
        if isCancelled { return }
        print("Performing cleanup in the background")
    }
}