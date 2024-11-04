import UIKit

// MARK: - UIView Extensions
extension UIView {
    
    // Add rounded corners to any view
    func roundCorners(radius: CGFloat) {
        self.layer.cornerRadius = radius
        self.layer.masksToBounds = true
    }
    
    // Add shadow to any view
    func addShadow(color: UIColor, opacity: Float, offset: CGSize, radius: CGFloat) {
        self.layer.shadowColor = color.cgColor
        self.layer.shadowOpacity = opacity
        self.layer.shadowOffset = offset
        self.layer.shadowRadius = radius
        self.layer.masksToBounds = false
    }
    
    // Add border to any view
    func addBorder(color: UIColor, width: CGFloat) {
        self.layer.borderColor = color.cgColor
        self.layer.borderWidth = width
    }
    
    // Set gradient background for a view
    func setGradientBackground(colorTop: UIColor, colorBottom: UIColor) {
        let gradientLayer = CAGradientLayer()
        gradientLayer.colors = [colorTop.cgColor, colorBottom.cgColor]
        gradientLayer.locations = [0.0, 1.0]
        gradientLayer.frame = bounds
        layer.insertSublayer(gradientLayer, at: 0)
    }
    
    // Make the view circular
    func makeCircular() {
        self.layer.cornerRadius = self.frame.size.width / 2
        self.clipsToBounds = true
    }
}

// MARK: - UIColor Extensions
extension UIColor {
    
    // Create UIColor from hex code
    convenience init(hex: String) {
        var hexString = hex.trimmingCharacters(in: .whitespacesAndNewlines).uppercased()
        
        if hexString.hasPrefix("#") {
            hexString.remove(at: hexString.startIndex)
        }
        
        if hexString.count != 6 {
            self.init(white: 0.5, alpha: 1.0)
            return
        }
        
        var rgbValue: UInt64 = 0
        Scanner(string: hexString).scanHexInt64(&rgbValue)
        
        self.init(
            red: CGFloat((rgbValue & 0xFF0000) >> 16) / 255.0,
            green: CGFloat((rgbValue & 0x00FF00) >> 8) / 255.0,
            blue: CGFloat(rgbValue & 0x0000FF) / 255.0,
            alpha: 1.0
        )
    }
}

// MARK: - UIImageView Extensions
extension UIImageView {
    
    // Load image from URL
    func loadImage(from url: URL) {
        DispatchQueue.global().async {
            if let data = try? Data(contentsOf: url) {
                if let image = UIImage(data: data) {
                    DispatchQueue.main.async {
                        self.image = image
                    }
                }
            }
        }
    }
    
    // Make the image view circular
    func makeCircular() {
        self.layer.cornerRadius = self.frame.size.width / 2
        self.clipsToBounds = true
    }
}

// MARK: - String Extensions
extension String {
    
    // Validate if string is a valid email
    var isValidEmail: Bool {
        let emailRegEx = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}"
        let emailTest = NSPredicate(format: "SELF MATCHES %@", emailRegEx)
        return emailTest.evaluate(with: self)
    }
    
    // Remove white spaces from a string
    var trimmed: String {
        return self.trimmingCharacters(in: .whitespacesAndNewlines)
    }
    
    // Check if string is a valid URL
    var isValidURL: Bool {
        return URL(string: self) != nil
    }
    
    // Localize string
    var localized: String {
        return NSLocalizedString(self, comment: "")
    }
    
    // Convert HTML string to NSAttributedString
    func htmlToAttributedString() -> NSAttributedString? {
        guard let data = data(using: .utf8) else { return nil }
        do {
            return try NSAttributedString(data: data, options: [.documentType: NSAttributedString.DocumentType.html, .characterEncoding: String.Encoding.utf8.rawValue], documentAttributes: nil)
        } catch {
            return nil
        }
    }
}

// MARK: - Date Extensions
extension Date {
    
    // Convert Date to string with a specific format
    func toString(format: String) -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = format
        return dateFormatter.string(from: self)
    }
    
    // Convert string to Date with a specific format
    static func fromString(_ dateString: String, format: String) -> Date? {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = format
        return dateFormatter.date(from: dateString)
    }
    
    // Get the start of the day for the current date
    var startOfDay: Date {
        return Calendar.current.startOfDay(for: self)
    }
    
    // Get the number of days between two dates
    func daysBetween(to date: Date) -> Int {
        return Calendar.current.dateComponents([.day], from: self, to: date).day ?? 0
    }
}

// MARK: - UserDefaults Extensions
extension UserDefaults {
    
    // Save Codable object to UserDefaults
    func setObject<T: Codable>(_ object: T, forKey: String) {
        let encoder = JSONEncoder()
        if let encoded = try? encoder.encode(object) {
            self.set(encoded, forKey: forKey)
        }
    }
    
    // Retrieve Codable object from UserDefaults
    func getObject<T: Codable>(forKey: String, castTo type: T.Type) -> T? {
        if let savedData = self.data(forKey: forKey) {
            let decoder = JSONDecoder()
            if let loadedObject = try? decoder.decode(type, from: savedData) {
                return loadedObject
            }
        }
        return nil
    }
    
    // Remove object from UserDefaults
    func removeObject(forKey: String) {
        self.removeObject(forKey: forKey)
    }
}