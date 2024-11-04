import UIKit
import UserNotifications

class SceneDelegate: UIResponder, UIWindowSceneDelegate {

    var window: UIWindow?

    func scene(_ scene: UIScene, willConnectTo session: UISceneSession, options connectionOptions: UIScene.ConnectionOptions) {
        guard let windowScene = (scene as? UIWindowScene) else { return }

        window = UIWindow(windowScene: windowScene)
        let rootViewController = MainViewController()
        let navigationController = UINavigationController(rootViewController: rootViewController)
        window?.rootViewController = navigationController
        window?.makeKeyAndVisible()
    }

    func sceneDidBecomeActive(_ scene: UIScene) {
        print("Scene became active")
        resumeTasks()
    }

    func sceneWillResignActive(_ scene: UIScene) {
        print("Scene will resign active")
        pauseTasks()
    }

    func sceneWillEnterForeground(_ scene: UIScene) {
        print("Scene will enter foreground")
        restoreAppState()
    }

    func sceneDidEnterBackground(_ scene: UIScene) {
        print("Scene did enter background")
        saveAppState()
    }

    // Save application state
    private func saveAppState() {
        print("Saving app state")
        saveUserPreferences()
    }

    // Restore application state
    private func restoreAppState() {
        print("Restoring app state")
        restoreUserPreferences()
    }

    // Pause tasks when the app is inactive
    private func pauseTasks() {
        print("Pausing tasks")
    }

    // Resume tasks when the app is active
    private func resumeTasks() {
        print("Resuming tasks")
    }

    // Save user preferences
    private func saveUserPreferences() {
        print("Saving user preferences")
    }

    // Restore user preferences
    private func restoreUserPreferences() {
        print("Restoring user preferences")
    }
}

// MARK: - Main ViewController
class MainViewController: UIViewController {
    
    private let button = CustomButton()

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .systemBackground
        setupUI()
    }

    private func setupUI() {
        view.addSubview(button)
        button.setTitle("Tap Me", for: .normal)
        button.translatesAutoresizingMaskIntoConstraints = false
        
        NSLayoutConstraint.activate([
            button.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            button.centerYAnchor.constraint(equalTo: view.centerYAnchor),
            button.widthAnchor.constraint(equalToConstant: 200),
            button.heightAnchor.constraint(equalToConstant: 50)
        ])
        
        button.addTarget(self, action: #selector(buttonTapped), for: .touchUpInside)
    }
    
    @objc private func buttonTapped() {
        let detailVC = MovieDetailViewController()
        navigationController?.pushViewController(detailVC, animated: true)
    }
}

// MARK: - Movie Detail ViewController
class MovieDetailViewController: UIViewController {
    
    private let titleLabel = UILabel()

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .systemBackground
        setupUI()
    }

    private func setupUI() {
        view.addSubview(titleLabel)
        titleLabel.text = "Movie Details"
        titleLabel.textAlignment = .center
        titleLabel.translatesAutoresizingMaskIntoConstraints = false
        
        NSLayoutConstraint.activate([
            titleLabel.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            titleLabel.centerYAnchor.constraint(equalTo: view.centerYAnchor),
            titleLabel.widthAnchor.constraint(equalToConstant: 300),
            titleLabel.heightAnchor.constraint(equalToConstant: 40)
        ])
    }
}

// MARK: - Custom Button Component
class CustomButton: UIButton {
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupButton()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setupButton()
    }
    
    private func setupButton() {
        backgroundColor = .systemBlue
        layer.cornerRadius = 10
        setTitleColor(.white, for: .normal)
        titleLabel?.font = UIFont.boldSystemFont(ofSize: 16)
    }
}

// MARK: - Network Handling in SceneDelegate
extension SceneDelegate {

    func observeNetworkChanges() {
        NotificationCenter.default.addObserver(self, selector: #selector(networkStatusChanged), name: .reachabilityChanged, object: nil)
    }

    @objc private func networkStatusChanged(_ notification: Notification) {
        print("Network status changed")
    }
}

// MARK: - Notification Handling in SceneDelegate
extension SceneDelegate {

    func registerForNotifications() {
        let center = UNUserNotificationCenter.current()
        center.requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
            if granted {
                print("Notifications allowed")
            } else {
                print("Notifications denied")
            }
        }
    }

    func handleIncomingNotification(_ userInfo: [AnyHashable: Any]) {
        print("Handling notification: \(userInfo)")
    }
}

// MARK: - Background Task Handling in SceneDelegate
extension SceneDelegate {

    func registerBackgroundTasks() {
        let taskId = UIApplication.shared.beginBackgroundTask(withName: "DataSync") {
            UIApplication.shared.endBackgroundTask(taskId)
        }
        
        DispatchQueue.global().async {
            sleep(5)
            UIApplication.shared.endBackgroundTask(taskId)
        }
    }
}

// MARK: - User Interaction
class ProfileViewController: UIViewController {
    
    private let nameLabel = UILabel()

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .white
        setupUI()
    }

    private func setupUI() {
        view.addSubview(nameLabel)
        nameLabel.text = "Profile"
        nameLabel.textAlignment = .center
        nameLabel.translatesAutoresizingMaskIntoConstraints = false
        
        NSLayoutConstraint.activate([
            nameLabel.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            nameLabel.centerYAnchor.constraint(equalTo: view.centerYAnchor),
            nameLabel.widthAnchor.constraint(equalToConstant: 200),
            nameLabel.heightAnchor.constraint(equalToConstant: 40)
        ])
    }
}

// MARK: - Additional Features for Handling System Events
extension SceneDelegate {

    func handleMemoryWarning() {
        print("Memory warning received")
    }

    func handleLowPowerMode() {
        print("Low Power Mode activated")
    }
}