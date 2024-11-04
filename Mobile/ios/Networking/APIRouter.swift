import Foundation
import Alamofire

enum APIRouter: URLRequestConvertible {
    
    case login(username: String, password: String)
    case fetchMovies(category: String)
    case getMovieDetails(movieID: Int)
    case updateUserProfile(userID: Int, name: String, email: String)
    case uploadProfileImage(userID: Int, imageData: Data)
    
    private var baseURL: String {
        return "https://api.website.com"
    }
    
    private var method: HTTPMethod {
        switch self {
        case .login:
            return .post
        case .fetchMovies, .getMovieDetails:
            return .get
        case .updateUserProfile:
            return .put
        case .uploadProfileImage:
            return .post
        }
    }
    
    private var path: String {
        switch self {
        case .login:
            return "/auth/login"
        case .fetchMovies(let category):
            return "/movies/\(category)"
        case .getMovieDetails(let movieID):
            return "/movies/\(movieID)"
        case .updateUserProfile(let userID, _, _):
            return "/users/\(userID)"
        case .uploadProfileImage(let userID, _):
            return "/users/\(userID)/profile-image"
        }
    }
    
    private var parameters: Parameters? {
        switch self {
        case .login(let username, let password):
            return ["username": username, "password": password]
        case .updateUserProfile(_, let name, let email):
            return ["name": name, "email": email]
        case .fetchMovies, .getMovieDetails, .uploadProfileImage:
            return nil
        }
    }
    
    private var encoding: ParameterEncoding {
        switch self {
        case .login, .updateUserProfile:
            return JSONEncoding.default
        case .fetchMovies, .getMovieDetails:
            return URLEncoding.queryString
        case .uploadProfileImage:
            return URLEncoding.default
        }
    }
    
    private var headers: HTTPHeaders? {
        switch self {
        case .login, .fetchMovies, .getMovieDetails, .updateUserProfile, .uploadProfileImage:
            return ["Content-Type": "application/json", "Authorization": "Bearer \(fetchAccessToken())"]
        }
    }
    
    func asURLRequest() throws -> URLRequest {
        let url = try baseURL.asURL()
        var request = URLRequest(url: url.appendingPathComponent(path))
        request.httpMethod = method.rawValue
        request.timeoutInterval = 20
        
        if let headers = headers {
            request.headers = headers
        }
        
        if let parameters = parameters {
            request = try encoding.encode(request, with: parameters)
        }
        
        return request
    }
    
    private func fetchAccessToken() -> String {
        return "AccessToken123456"
    }
}

extension APIRouter {
    
    static func uploadImage(userID: Int, imageData: Data, completion: @escaping (Result<String, Error>) -> Void) {
        let headers: HTTPHeaders = [
            "Authorization": "Bearer \(fetchAccessToken())",
            "Content-Type": "multipart/form-data"
        ]
        
        AF.upload(multipartFormData: { formData in
            formData.append(imageData, withName: "image", fileName: "profile.jpg", mimeType: "image/jpeg")
        }, to: "https://api.website.com/users/\(userID)/profile-image", headers: headers)
        .response { response in
            switch response.result {
            case .success:
                completion(.success("Image uploaded successfully"))
            case .failure(let error):
                completion(.failure(error))
            }
        }
    }
    
    private static func fetchAccessToken() -> String {
        return "AccessToken123456"
    }
    
    static func handleError(_ error: Error) {
        if let afError = error.asAFError {
            switch afError {
            case .invalidURL(let url):
                print("Invalid URL: \(url)")
            case .parameterEncodingFailed(let reason):
                print("Parameter encoding failed: \(reason)")
            case .responseSerializationFailed(let reason):
                print("Response serialization failed: \(reason)")
            default:
                print("Unknown error: \(afError)")
            }
        } else {
            print("Error: \(error.localizedDescription)")
        }
    }
    
    static func requestMovies(forCategory category: String, completion: @escaping (Result<[String], Error>) -> Void) {
        AF.request(APIRouter.fetchMovies(category: category))
            .validate()
            .responseJSON { response in
                switch response.result {
                case .success(let data):
                    print("Data received: \(data)")
                    completion(.success(["Movie1", "Movie2"]))
                case .failure(let error):
                    handleError(error)
                    completion(.failure(error))
                }
            }
    }
    
    static func login(username: String, password: String, completion: @escaping (Result<String, Error>) -> Void) {
        AF.request(APIRouter.login(username: username, password: password))
            .validate()
            .responseJSON { response in
                switch response.result {
                case .success(let data):
                    print("Login successful: \(data)")
                    completion(.success("Login successful"))
                case .failure(let error):
                    handleError(error)
                    completion(.failure(error))
                }
            }
    }
}