import Foundation
import UIKit

// MARK: - API Endpoints
struct APIConstants {
    static let baseURL = "https://api.website.com"
    
    struct Endpoints {
        static let login = "/auth/login"
        static let register = "/auth/register"
        static let fetchMovies = "/movies"
        static let fetchMovieDetails = "/movies/{id}"
        static let fetchRecommendations = "/recommendations"
        static let userProfile = "/user/profile"
    }
    
    struct Headers {
        static let contentType = "Content-Type"
        static let authorization = "Authorization"
        static let applicationJson = "application/json"
    }
}

// MARK: - API Keys
struct APIKeys {
    static let movieDBKey = "MOVIEDB_API_KEY"
    static let googleAnalyticsKey = "GOOGLE_ANALYTICS_KEY"
}

// MARK: - User Defaults Keys
struct UserDefaultsKeys {
    static let userToken = "USER_TOKEN"
    static let userLoggedIn = "USER_LOGGED_IN"
    static let userId = "USER_ID"
    static let preferredLanguage = "PREFERRED_LANGUAGE"
}

// MARK: - Notification Constants
struct NotificationConstants {
    static let movieUpdated = Notification.Name("movieUpdated")
    static let profileUpdated = Notification.Name("profileUpdated")
    static let loginStatusChanged = Notification.Name("loginStatusChanged")
}

// MARK: - UI Constants
struct UIConstants {
    static let primaryColor = UIColor(red: 230/255, green: 57/255, blue: 70/255, alpha: 1.0)
    static let secondaryColor = UIColor(red: 29/255, green: 53/255, blue: 87/255, alpha: 1.0)
    static let backgroundColor = UIColor(red: 255/255, green: 250/255, blue: 240/255, alpha: 1.0)
    static let accentColor = UIColor(red: 6/255, green: 214/255, blue: 160/255, alpha: 1.0)

    struct Fonts {
        static let titleFont = UIFont.systemFont(ofSize: 24, weight: .bold)
        static let bodyFont = UIFont.systemFont(ofSize: 16, weight: .regular)
        static let buttonFont = UIFont.systemFont(ofSize: 18, weight: .medium)
    }
}

// MARK: - Error Messages
struct ErrorMessages {
    static let networkError = "Unable to connect. Please check your internet connection."
    static let serverError = "Server is currently unavailable. Please try again later."
    static let invalidCredentials = "Invalid email or password. Please try again."
    static let missingData = "Required data is missing. Please try again."
}

// MARK: - Success Messages
struct SuccessMessages {
    static let loginSuccess = "Successfully logged in."
    static let registrationSuccess = "Registration successful. Welcome!"
    static let movieFetchSuccess = "Movies loaded successfully."
    static let profileUpdateSuccess = "Profile updated successfully."
}

// MARK: - App Settings
struct AppSettings {
    static let defaultLanguage = "en"
    static let supportedLanguages = ["en", "es", "fr", "de", "jp"]
    static let maxRetries = 3
    static let retryDelay = 2.0 // in seconds
}

// MARK: - App Identifiers
struct AppIdentifiers {
    static let appBundleId = "com.website.netflixclone"
    static let appGroupId = "group.com.website.netflixclone"
    static let pushNotificationId = "PUSH_NOTIFICATION_ID"
    static let analyticsTrackingId = "ANALYTICS_TRACKING_ID"
}

// MARK: - File Paths
struct FilePaths {
    static let documentsDirectory = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true).first
    static let tempDirectory = NSTemporaryDirectory()
    static let cacheDirectory = NSSearchPathForDirectoriesInDomains(.cachesDirectory, .userDomainMask, true).first
}

// MARK: - Date Formats
struct DateFormats {
    static let displayDate = "MMM d, yyyy"
    static let iso8601Full = "yyyy-MM-dd'T'HH:mm:ssZ"
    static let timeOnly = "HH:mm"
}

// MARK: - Timeouts
struct Timeouts {
    static let networkTimeout: TimeInterval = 30.0
    static let imageDownloadTimeout: TimeInterval = 60.0
}

// MARK: - Animation Durations
struct AnimationDurations {
    static let fadeIn: TimeInterval = 0.3
    static let fadeOut: TimeInterval = 0.3
    static let slideIn: TimeInterval = 0.4
    static let slideOut: TimeInterval = 0.4
}

// MARK: - App Dimensions
struct AppDimensions {
    static let cornerRadius: CGFloat = 10.0
    static let buttonHeight: CGFloat = 50.0
    static let padding: CGFloat = 16.0
    static let margin: CGFloat = 12.0
}

// MARK: - Device Types
struct DeviceTypes {
    static let isiPhone = UIDevice.current.userInterfaceIdiom == .phone
    static let isiPad = UIDevice.current.userInterfaceIdiom == .pad
}

// MARK: - API Error Codes
struct APIErrorCodes {
    static let unauthorized = 401
    static let notFound = 404
    static let serverError = 500
}

// MARK: - App URLs
struct AppURLs {
    static let termsOfService = "https://website.com/terms"
    static let privacyPolicy = "https://website.com/privacy"
    static let contactSupport = "https://website.com/support"
}

// MARK: - Image Names
struct ImageNames {
    static let placeholder = "placeholder_image"
    static let profileIcon = "profile_icon"
    static let moviePosterPlaceholder = "movie_poster_placeholder"
}

// MARK: - Video Player Constants
struct VideoPlayerConstants {
    static let playbackRateNormal = 1.0
    static let playbackRateFast = 1.5
    static let playbackRateSlow = 0.75
    static let seekForwardDuration: TimeInterval = 10.0
    static let seekBackwardDuration: TimeInterval = 10.0
}

// MARK: - Authentication Constants
struct AuthConstants {
    static let minPasswordLength = 8
    static let maxPasswordLength = 64
    static let minUsernameLength = 3
    static let maxUsernameLength = 30
}

// MARK: - App Features Flags
struct FeatureFlags {
    static let enableRecommendations = true
    static let enableOfflineMode = false
    static let enableAnalytics = true
}