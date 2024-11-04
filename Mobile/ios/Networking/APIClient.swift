import Foundation

// MARK: - APIError Enum for handling network-related errors
enum APIError: Error {
    case invalidURL
    case decodingError
    case networkError(Error)
    case serverError(Int)
    case unknownError
}

// MARK: - APIClient Class
final class APIClient {

    // MARK: - Singleton Instance
    static let shared = APIClient()

    // MARK: - URLSession Property
    private let session: URLSession

    // MARK: - Initializer
    private init() {
        let configuration = URLSessionConfiguration.default
        configuration.timeoutIntervalForRequest = 30
        configuration.timeoutIntervalForResource = 60
        session = URLSession(configuration: configuration)
    }

    // MARK: - Generic Request Method
    func performRequest<T: Decodable>(with router: APIRouter, completion: @escaping (Result<T, APIError>) -> Void) {
        do {
            let request = try router.asURLRequest()
            session.dataTask(with: request) { (data, response, error) in
                if let error = error {
                    completion(.failure(.networkError(error)))
                    return
                }

                guard let httpResponse = response as? HTTPURLResponse else {
                    completion(.failure(.unknownError))
                    return
                }

                guard 200..<300 ~= httpResponse.statusCode else {
                    completion(.failure(.serverError(httpResponse.statusCode)))
                    return
                }

                guard let data = data else {
                    completion(.failure(.unknownError))
                    return
                }

                do {
                    let decodedResponse = try JSONDecoder().decode(T.self, from: data)
                    completion(.success(decodedResponse))
                } catch {
                    completion(.failure(.decodingError))
                }
            }.resume()
        } catch {
            completion(.failure(.invalidURL))
        }
    }

    // MARK: - Upload Method for File Upload
    func uploadRequest<T: Decodable>(with router: APIRouter, fileData: Data, completion: @escaping (Result<T, APIError>) -> Void) {
        do {
            var request = try router.asURLRequest()
            request.httpMethod = "POST"
            request.setValue("multipart/form-data", forHTTPHeaderField: "Content-Type")

            session.uploadTask(with: request, from: fileData) { (data, response, error) in
                if let error = error {
                    completion(.failure(.networkError(error)))
                    return
                }

                guard let httpResponse = response as? HTTPURLResponse else {
                    completion(.failure(.unknownError))
                    return
                }

                guard 200..<300 ~= httpResponse.statusCode else {
                    completion(.failure(.serverError(httpResponse.statusCode)))
                    return
                }

                guard let data = data else {
                    completion(.failure(.unknownError))
                    return
                }

                do {
                    let decodedResponse = try JSONDecoder().decode(T.self, from: data)
                    completion(.success(decodedResponse))
                } catch {
                    completion(.failure(.decodingError))
                }
            }.resume()
        } catch {
            completion(.failure(.invalidURL))
        }
    }

    // MARK: - Download Method for Downloading Files
    func downloadRequest(with router: APIRouter, completion: @escaping (Result<URL, APIError>) -> Void) {
        do {
            let request = try router.asURLRequest()
            session.downloadTask(with: request) { (tempURL, response, error) in
                if let error = error {
                    completion(.failure(.networkError(error)))
                    return
                }

                guard let httpResponse = response as? HTTPURLResponse else {
                    completion(.failure(.unknownError))
                    return
                }

                guard 200..<300 ~= httpResponse.statusCode else {
                    completion(.failure(.serverError(httpResponse.statusCode)))
                    return
                }

                guard let tempURL = tempURL else {
                    completion(.failure(.unknownError))
                    return
                }

                completion(.success(tempURL))
            }.resume()
        } catch {
            completion(.failure(.invalidURL))
        }
    }

    // MARK: - GET Method for fetching data
    func get<T: Decodable>(from endpoint: APIRouter, completion: @escaping (Result<T, APIError>) -> Void) {
        performRequest(with: endpoint, completion: completion)
    }

    // MARK: - POST Method for sending data
    func post<T: Decodable>(to endpoint: APIRouter, body: [String: Any], completion: @escaping (Result<T, APIError>) -> Void) {
        performRequest(with: endpoint, completion: completion)
    }

    // MARK: - PUT Method for updating data
    func put<T: Decodable>(to endpoint: APIRouter, body: [String: Any], completion: @escaping (Result<T, APIError>) -> Void) {
        performRequest(with: endpoint, completion: completion)
    }

    // MARK: - DELETE Method for deleting data
    func delete<T: Decodable>(from endpoint: APIRouter, completion: @escaping (Result<T, APIError>) -> Void) {
        performRequest(with: endpoint, completion: completion)
    }

    // MARK: - Helper Method for constructing request body
    private func createRequestBody(parameters: [String: Any]) -> Data? {
        return try? JSONSerialization.data(withJSONObject: parameters, options: .prettyPrinted)
    }
}

// MARK: - APIRouter Enum for defining API Endpoints
enum APIRouter: URLRequestConvertible {

    case login(email: String, password: String)
    case fetchMovies
    case movieDetails(id: String)

    // MARK: - URLRequestConvertible
    func asURLRequest() throws -> URLRequest {
        let url = try baseURL.asURL()

        var request = URLRequest(url: url.appendingPathComponent(path))
        request.httpMethod = method.rawValue
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        switch self {
        case .login(let email, let password):
            let parameters = ["email": email, "password": password]
            request.httpBody = try JSONSerialization.data(withJSONObject: parameters, options: .prettyPrinted)

        case .movieDetails, .fetchMovies:
            break
        }

        return request
    }

    // MARK: - Base URL
    var baseURL: String {
        return "https://api.website.com"
    }

    // MARK: - HTTP Method
    var method: HTTPMethod {
        switch self {
        case .login:
            return .post
        case .fetchMovies, .movieDetails:
            return .get
        }
    }

    // MARK: - Path
    var path: String {
        switch self {
        case .login:
            return "/login"
        case .fetchMovies:
            return "/movies"
        case .movieDetails(let id):
            return "/movies/\(id)"
        }
    }
}

// MARK: - URLRequestConvertible Protocol
protocol URLRequestConvertible {
    func asURLRequest() throws -> URLRequest
}

// MARK: - HTTPMethod Enum
enum HTTPMethod: String {
    case get = "GET"
    case post = "POST"
    case put = "PUT"
    case delete = "DELETE"
}