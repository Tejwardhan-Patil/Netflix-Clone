import Foundation

enum UserType: String, Codable {
    case basic = "Basic"
    case premium = "Premium"
    case admin = "Admin"
}

enum UserStatus: String, Codable {
    case active = "Active"
    case suspended = "Suspended"
    case deactivated = "Deactivated"
}

struct Profile: Codable {
    let profileID: UUID
    var name: String
    var avatarURL: URL?
    var isKidProfile: Bool
    
    init(name: String, avatarURL: URL? = nil, isKidProfile: Bool = false) {
        self.profileID = UUID()
        self.name = name
        self.avatarURL = avatarURL
        self.isKidProfile = isKidProfile
    }
}

struct PaymentDetails: Codable {
    var cardNumber: String
    var expiryDate: String
    var cardholderName: String
    var billingAddress: String
}

struct User: Codable {
    let userID: UUID
    var email: String
    var passwordHash: String
    var userType: UserType
    var status: UserStatus
    var paymentDetails: PaymentDetails?
    var profiles: [Profile]
    var createdAt: Date
    var lastLogin: Date?
    var subscriptionExpiry: Date?
    
    init(email: String, passwordHash: String, userType: UserType = .basic, status: UserStatus = .active, paymentDetails: PaymentDetails? = nil, profiles: [Profile] = []) {
        self.userID = UUID()
        self.email = email
        self.passwordHash = passwordHash
        self.userType = userType
        self.status = status
        self.paymentDetails = paymentDetails
        self.profiles = profiles
        self.createdAt = Date()
        self.lastLogin = nil
        self.subscriptionExpiry = nil
    }
    
    mutating func addProfile(_ profile: Profile) {
        profiles.append(profile)
    }
    
    mutating func removeProfile(profileID: UUID) {
        profiles.removeAll { $0.profileID == profileID }
    }
    
    mutating func updateProfile(profileID: UUID, name: String, avatarURL: URL?, isKidProfile: Bool) {
        if let index = profiles.firstIndex(where: { $0.profileID == profileID }) {
            profiles[index].name = name
            profiles[index].avatarURL = avatarURL
            profiles[index].isKidProfile = isKidProfile
        }
    }
    
    mutating func updateLastLogin() {
        self.lastLogin = Date()
    }
    
    mutating func renewSubscription(for months: Int) {
        if let expiryDate = subscriptionExpiry {
            subscriptionExpiry = Calendar.current.date(byAdding: .month, value: months, to: expiryDate)
        } else {
            subscriptionExpiry = Calendar.current.date(byAdding: .month, value: months, to: Date())
        }
    }
    
    mutating func suspendUser() {
        status = .suspended
    }
    
    mutating func deactivateUser() {
        status = .deactivated
    }
    
    mutating func activateUser() {
        status = .active
    }
    
    func getActiveProfiles() -> [Profile] {
        return profiles.filter { $0.isKidProfile == false }
    }
    
    func getKidProfiles() -> [Profile] {
        return profiles.filter { $0.isKidProfile == true }
    }
}

// Extensions to help with validation

extension User {
    static func isValidEmail(_ email: String) -> Bool {
        let emailRegEx = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}"
        let emailTest = NSPredicate(format: "SELF MATCHES %@", emailRegEx)
        return emailTest.evaluate(with: email)
    }
    
    static func isValidPassword(_ password: String) -> Bool {
        return password.count >= 8
    }
}

extension User {
    func isSubscriptionActive() -> Bool {
        guard let expiryDate = subscriptionExpiry else { return false }
        return expiryDate > Date()
    }
}

// Usage
let payment = PaymentDetails(cardNumber: "1234 5678 9012 3456", expiryDate: "12/25", cardholderName: "Person", billingAddress: "123 Main St")

var user = User(email: "person@website.com", passwordHash: "hashedpassword", paymentDetails: payment)

let profile1 = Profile(name: "Person1", avatarURL: URL(string: "https://website.com/avatar1.png"))
let profile2 = Profile(name: "Person2", isKidProfile: true)

user.addProfile(profile1)
user.addProfile(profile2)

print("Active Profiles: \(user.getActiveProfiles().map { $0.name })")
print("Kid Profiles: \(user.getKidProfiles().map { $0.name })")

// Outputting user information
print("User ID: \(user.userID)")
print("Email: \(user.email)")
print("Subscription Status: \(user.isSubscriptionActive() ? "Active" : "Expired")")