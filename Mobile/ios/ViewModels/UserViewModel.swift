import Foundation
import Combine

// User model representing the user's data
struct User: Codable {
    var id: String
    var name: String
    var email: String
    var profileImageUrl: String
}

// Enum representing different states of the view
enum UserViewState {
    case idle
    case loading
    case loaded(User)
    case error(Error)
}

// Enum for user-related errors
enum UserViewModelError: Error {
    case invalidCredentials
    case networkError(Error)
    case userNotFound
    case unknownError
}

// Protocol to define networking services for User
protocol UserServiceProtocol {
    func fetchUser(byId id: String) -> AnyPublisher<User, Error>
    func updateUser(user: User) -> AnyPublisher<User, Error>
    func deleteUser(byId id: String) -> AnyPublisher<Void, Error>
    func loginUser(email: String, password: String) -> AnyPublisher<User, Error>
}

// ViewModel for managing the user
class UserViewModel: ObservableObject {
    @Published var user: User?
    @Published var viewState: UserViewState = .idle
    @Published var errorMessage: String?
    
    private var cancellables = Set<AnyCancellable>()
    private var userService: UserServiceProtocol

    // Dependency injection for the service
    init(userService: UserServiceProtocol) {
        self.userService = userService
    }

    // Function to fetch user data
    func fetchUser(byId id: String) {
        self.viewState = .loading
        
        userService.fetchUser(byId: id)
            .sink(receiveCompletion: { [weak self] completion in
                switch completion {
                case .finished:
                    break
                case .failure(let error):
                    self?.handleError(error)
                }
            }, receiveValue: { [weak self] user in
                self?.user = user
                self?.viewState = .loaded(user)
            })
            .store(in: &cancellables)
    }

    // Function to update the user information
    func updateUser(name: String?, email: String?, profileImageUrl: String?) {
        guard var currentUser = self.user else {
            self.handleError(UserViewModelError.userNotFound)
            return
        }
        
        if let name = name {
            currentUser.name = name
        }
        if let email = email {
            currentUser.email = email
        }
        if let profileImageUrl = profileImageUrl {
            currentUser.profileImageUrl = profileImageUrl
        }
        
        self.viewState = .loading
        
        userService.updateUser(user: currentUser)
            .sink(receiveCompletion: { [weak self] completion in
                switch completion {
                case .finished:
                    break
                case .failure(let error):
                    self?.handleError(error)
                }
            }, receiveValue: { [weak self] updatedUser in
                self?.user = updatedUser
                self?.viewState = .loaded(updatedUser)
            })
            .store(in: &cancellables)
    }
    
    // Function to delete a user
    func deleteUser() {
        guard let userId = self.user?.id else {
            self.handleError(UserViewModelError.userNotFound)
            return
        }
        
        self.viewState = .loading
        
        userService.deleteUser(byId: userId)
            .sink(receiveCompletion: { [weak self] completion in
                switch completion {
                case .finished:
                    self?.user = nil
                    self?.viewState = .idle
                case .failure(let error):
                    self?.handleError(error)
                }
            }, receiveValue: { _ in })
            .store(in: &cancellables)
    }
    
    // Function to log in the user
    func loginUser(email: String, password: String) {
        self.viewState = .loading
        
        userService.loginUser(email: email, password: password)
            .sink(receiveCompletion: { [weak self] completion in
                switch completion {
                case .finished:
                    break
                case .failure(let error):
                    self?.handleError(error)
                }
            }, receiveValue: { [weak self] user in
                self?.user = user
                self?.viewState = .loaded(user)
            })
            .store(in: &cancellables)
    }
    
    // Function to clear the user information
    func clearUserData() {
        self.user = nil
        self.viewState = .idle
    }
    
    // Function to handle errors
    private func handleError(_ error: Error) {
        if let error = error as? UserViewModelError {
            switch error {
            case .invalidCredentials:
                self.errorMessage = "Invalid credentials"
            case .networkError(let networkError):
                self.errorMessage = networkError.localizedDescription
            case .userNotFound:
                self.errorMessage = "User not found"
            case .unknownError:
                self.errorMessage = "An unknown error occurred"
            }
        } else {
            self.errorMessage = error.localizedDescription
        }
        self.viewState = .error(error)
    }
}

// Mock UserService implementation for testing
class MockUserService: UserServiceProtocol {
    func fetchUser(byId id: String) -> AnyPublisher<User, Error> {
        let user = User(id: id, name: "Person", email: "person@website.com", profileImageUrl: "http://website.com/image.jpg")
        return Just(user)
            .setFailureType(to: Error.self)
            .eraseToAnyPublisher()
    }
    
    func updateUser(user: User) -> AnyPublisher<User, Error> {
        return Just(user)
            .setFailureType(to: Error.self)
            .eraseToAnyPublisher()
    }
    
    func deleteUser(byId id: String) -> AnyPublisher<Void, Error> {
        return Just(())
            .setFailureType(to: Error.self)
            .eraseToAnyPublisher()
    }
    
    func loginUser(email: String, password: String) -> AnyPublisher<User, Error> {
        if email == "person@website.com" && password == "password" {
            let user = User(id: "123", name: "Person", email: email, profileImageUrl: "http://website.com/image.jpg")
            return Just(user)
                .setFailureType(to: Error.self)
                .eraseToAnyPublisher()
        } else {
            return Fail(error: UserViewModelError.invalidCredentials)
                .eraseToAnyPublisher()
        }
    }
}