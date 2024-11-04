import UIKit

class CustomButton: UIButton {

    // Enum to handle button states
    enum ButtonState {
        case normal
        case loading
        case disabled
    }

    // Properties
    private var originalButtonText: String?
    private var activityIndicator: UIActivityIndicatorView!
    private var buttonState: ButtonState = .normal

    // Initializer
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupButton()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setupButton()
    }

    // Setup button styles and UI elements
    private func setupButton() {
        layer.cornerRadius = 10
        backgroundColor = .systemBlue
        setTitleColor(.white, for: .normal)
        titleLabel?.font = UIFont.boldSystemFont(ofSize: 18)

        addShadow()
        setupActivityIndicator()
    }

    // Adding shadow to the button
    private func addShadow() {
        layer.shadowColor = UIColor.black.cgColor
        layer.shadowOffset = CGSize(width: 0.0, height: 2.0)
        layer.shadowOpacity = 0.3
        layer.shadowRadius = 3
        clipsToBounds = false
        layer.masksToBounds = false
    }

    // Activity Indicator to show loading state
    private func setupActivityIndicator() {
        activityIndicator = UIActivityIndicatorView(style: .medium)
        activityIndicator.color = .white
        activityIndicator.translatesAutoresizingMaskIntoConstraints = false
        addSubview(activityIndicator)
        activityIndicator.centerYAnchor.constraint(equalTo: centerYAnchor).isActive = true
        activityIndicator.centerXAnchor.constraint(equalTo: centerXAnchor).isActive = true
        activityIndicator.isHidden = true
    }

    // Button Press Animation
    override var isHighlighted: Bool {
        didSet {
            animateButtonPress(isHighlighted)
        }
    }

    private func animateButtonPress(_ highlighted: Bool) {
        UIView.animate(withDuration: 0.1, animations: {
            self.alpha = highlighted ? 0.75 : 1.0
        })
    }

    // Update the button state
    func updateState(_ state: ButtonState) {
        self.buttonState = state
        switch state {
        case .normal:
            setNormalState()
        case .loading:
            setLoadingState()
        case .disabled:
            setDisabledState()
        }
    }

    // Set normal state appearance
    private func setNormalState() {
        activityIndicator.stopAnimating()
        activityIndicator.isHidden = true
        setTitle(originalButtonText, for: .normal)
        isEnabled = true
        alpha = 1.0
        backgroundColor = .systemBlue
    }

    // Set loading state appearance
    private func setLoadingState() {
        originalButtonText = titleLabel?.text
        setTitle("", for: .normal)
        activityIndicator.isHidden = false
        activityIndicator.startAnimating()
        isEnabled = false
        alpha = 0.7
    }

    // Set disabled state appearance
    private func setDisabledState() {
        setTitle(originalButtonText, for: .normal)
        isEnabled = false
        alpha = 0.5
        backgroundColor = .gray
    }

    // Pulse Animation for button
    func addPulseAnimation() {
        let pulse = CASpringAnimation(keyPath: "transform.scale")
        pulse.duration = 0.6
        pulse.fromValue = 0.95
        pulse.toValue = 1.0
        pulse.autoreverses = true
        pulse.repeatCount = 2
        pulse.initialVelocity = 0.5
        pulse.damping = 1.0

        layer.add(pulse, forKey: "pulse")
    }

    // Flash Animation for attention
    func addFlashAnimation() {
        let flash = CABasicAnimation(keyPath: "opacity")
        flash.duration = 0.5
        flash.fromValue = 1
        flash.toValue = 0.1
        flash.timingFunction = CAMediaTimingFunction(name: .easeInEaseOut)
        flash.autoreverses = true
        flash.repeatCount = 3

        layer.add(flash, forKey: "flash")
    }

    // Shake Animation for error state
    func addShakeAnimation() {
        let shake = CABasicAnimation(keyPath: "position")
        shake.duration = 0.1
        shake.repeatCount = 2
        shake.autoreverses = true

        let fromPoint = CGPoint(x: center.x - 5, y: center.y)
        let fromValue = NSValue(cgPoint: fromPoint)

        let toPoint = CGPoint(x: center.x + 5, y: center.y)
        let toValue = NSValue(cgPoint: toPoint)

        shake.fromValue = fromValue
        shake.toValue = toValue

        layer.add(shake, forKey: "shake")
    }
}

// MARK: - Button Theme Customization
extension CustomButton {

    // Theme Styles
    enum ButtonTheme {
        case primary
        case secondary
        case danger
    }

    // Apply theme based on the button type
    func applyTheme(_ theme: ButtonTheme) {
        switch theme {
        case .primary:
            backgroundColor = .systemBlue
            setTitleColor(.white, for: .normal)
        case .secondary:
            backgroundColor = .systemGray
            setTitleColor(.black, for: .normal)
        case .danger:
            backgroundColor = .systemRed
            setTitleColor(.white, for: .normal)
        }
    }

    // Update Font Style
    func setFontStyle(_ font: UIFont) {
        titleLabel?.font = font
    }

    // Update Corner Radius
    func setCornerRadius(_ radius: CGFloat) {
        layer.cornerRadius = radius
    }

    // Update Shadow Opacity
    func setShadowOpacity(_ opacity: Float) {
        layer.shadowOpacity = opacity
    }
}

// MARK: - Interface Builder Customization
@IBDesignable
class DesignableButton: CustomButton {

    @IBInspectable var theme: String = "primary" {
        didSet {
            if let themeEnum = ButtonTheme(rawValue: theme.lowercased()) {
                applyTheme(themeEnum)
            }
        }
    }

    @IBInspectable var customFont: UIFont = UIFont.systemFont(ofSize: 18) {
        didSet {
            setFontStyle(customFont)
        }
    }

    @IBInspectable var customCornerRadius: CGFloat = 10 {
        didSet {
            setCornerRadius(customCornerRadius)
        }
    }

    @IBInspectable var shadowOpacity: Float = 0.3 {
        didSet {
            setShadowOpacity(shadowOpacity)
        }
    }
}