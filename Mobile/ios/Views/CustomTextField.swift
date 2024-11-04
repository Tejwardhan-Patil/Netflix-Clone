import UIKit

// Enum for different validation types
enum ValidationType {
    case email
    case password
    case none
}

// Delegate for custom text field to handle validation and text change
protocol CustomTextFieldDelegate: AnyObject {
    func customTextFieldDidChange(_ textField: CustomTextField)
    func customTextFieldDidEndEditing(_ textField: CustomTextField, validationResult: Bool)
}

// Custom TextField with additional features like validation, error message, and styling
@IBDesignable
class CustomTextField: UITextField {

    // Delegate for handling events
    weak var customDelegate: CustomTextFieldDelegate?
    
    // Label for showing error messages
    private var errorLabel: UILabel = {
        let label = UILabel()
        label.textColor = .red
        label.font = UIFont.systemFont(ofSize: 12)
        label.isHidden = true
        return label
    }()
    
    // Padding for text within the text field
    var textPadding = UIEdgeInsets(top: 10, left: 10, bottom: 10, right: 10)
    
    // Validation type (email, password, etc)
    var validationType: ValidationType = .none
    
    // Indicates whether to show the error label or not
    var showError: Bool = false {
        didSet {
            errorLabel.isHidden = !showError
        }
    }
    
    // Custom placeholder text color
    @IBInspectable var placeholderTextColor: UIColor? {
        didSet {
            guard let color = placeholderTextColor else { return }
            attributedPlaceholder = NSAttributedString(string: placeholder ?? "", attributes: [.foregroundColor: color])
        }
    }

    // Error message to display
    @IBInspectable var errorMessage: String? {
        didSet {
            errorLabel.text = errorMessage
        }
    }
    
    // Border colors
    @IBInspectable var normalBorderColor: UIColor = .lightGray
    @IBInspectable var errorBorderColor: UIColor = .red

    // Setup method for configuring the view
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupView()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setupView()
    }
    
    // Setup view appearance and add error label
    private func setupView() {
        borderStyle = .roundedRect
        layer.borderWidth = 1.0
        layer.borderColor = normalBorderColor.cgColor
        font = UIFont.systemFont(ofSize: 16)
        
        // Adding error label below the text field
        addSubview(errorLabel)
        setupErrorLabelConstraints()
        
        addTarget(self, action: #selector(textFieldDidChange), for: .editingChanged)
        addTarget(self, action: #selector(textFieldDidEnd), for: .editingDidEnd)
    }
    
    // Layout for padding within the text field
    override func textRect(forBounds bounds: CGRect) -> CGRect {
        return bounds.inset(by: textPadding)
    }

    override func editingRect(forBounds bounds: CGRect) -> CGRect {
        return bounds.inset(by: textPadding)
    }

    override func placeholderRect(forBounds bounds: CGRect) -> CGRect {
        return bounds.inset(by: textPadding)
    }
    
    // Set error label constraints below the text field
    private func setupErrorLabelConstraints() {
        errorLabel.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            errorLabel.topAnchor.constraint(equalTo: self.bottomAnchor, constant: 4),
            errorLabel.leadingAnchor.constraint(equalTo: self.leadingAnchor),
            errorLabel.trailingAnchor.constraint(equalTo: self.trailingAnchor)
        ])
    }
    
    // Handle text change and notify delegate
    @objc private func textFieldDidChange() {
        customDelegate?.customTextFieldDidChange(self)
        showError = false
        layer.borderColor = normalBorderColor.cgColor
    }
    
    // Handle end of editing and validate input
    @objc private func textFieldDidEnd() {
        let isValid = validateInput()
        customDelegate?.customTextFieldDidEndEditing(self, validationResult: isValid)
        layer.borderColor = isValid ? normalBorderColor.cgColor : errorBorderColor.cgColor
        showError = !isValid
    }
    
    // Validation logic based on the type of input
    private func validateInput() -> Bool {
        guard let text = text, !text.isEmpty else {
            return false
        }
        
        switch validationType {
        case .email:
            return validateEmail(text)
        case .password:
            return validatePassword(text)
        case .none:
            return true
        }
    }
    
    // Email validation using regex
    private func validateEmail(_ email: String) -> Bool {
        let emailRegex = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}"
        let emailPred = NSPredicate(format:"SELF MATCHES %@", emailRegex)
        return emailPred.evaluate(with: email)
    }
    
    // Password validation (minimum 8 characters, 1 uppercase, 1 digit)
    private func validatePassword(_ password: String) -> Bool {
        let passwordRegex = "^(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$"
        let passwordPred = NSPredicate(format: "SELF MATCHES %@", passwordRegex)
        return passwordPred.evaluate(with: password)
    }
}

// Usage of the CustomTextField in a view controller
class ViewController: UIViewController, CustomTextFieldDelegate {

    private var emailTextField: CustomTextField!
    private var passwordTextField: CustomTextField!

    override func viewDidLoad() {
        super.viewDidLoad()

        // Initialize email text field
        emailTextField = CustomTextField()
        emailTextField.placeholder = "Enter your email"
        emailTextField.validationType = .email
        emailTextField.errorMessage = "Invalid email address"
        emailTextField.customDelegate = self
        
        // Initialize password text field
        passwordTextField = CustomTextField()
        passwordTextField.placeholder = "Enter your password"
        passwordTextField.validationType = .password
        passwordTextField.errorMessage = "Password must be at least 8 characters, 1 uppercase and 1 digit"
        passwordTextField.customDelegate = self

        // Layout and add text fields to the view
        view.addSubview(emailTextField)
        view.addSubview(passwordTextField)
        setupConstraints()
    }

    // Setting up constraints for the text fields
    private func setupConstraints() {
        emailTextField.translatesAutoresizingMaskIntoConstraints = false
        passwordTextField.translatesAutoresizingMaskIntoConstraints = false

        NSLayoutConstraint.activate([
            emailTextField.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: 20),
            emailTextField.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 16),
            emailTextField.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -16),
            passwordTextField.topAnchor.constraint(equalTo: emailTextField.bottomAnchor, constant: 20),
            passwordTextField.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 16),
            passwordTextField.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -16)
        ])
    }

    // Delegate method when text changes
    func customTextFieldDidChange(_ textField: CustomTextField) {
        // Handle text change
        print("Text changed in: \(textField)")
    }

    // Delegate method when editing ends
    func customTextFieldDidEndEditing(_ textField: CustomTextField, validationResult: Bool) {
        if validationResult {
            print("\(textField.placeholder ?? "") is valid")
        } else {
            print("\(textField.placeholder ?? "") is invalid")
        }
    }
}