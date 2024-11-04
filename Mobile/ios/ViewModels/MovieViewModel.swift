import Foundation
import Combine

// Movie model
struct Movie: Codable, Identifiable {
    let id: Int
    let title: String
    let overview: String
    let posterPath: String
    let releaseDate: String
    let rating: Double

    enum CodingKeys: String, CodingKey {
        case id
        case title
        case overview
        case posterPath = "poster_path"
        case releaseDate = "release_date"
        case rating = "vote_average"
    }
}

// API Client to handle network requests
class APIClient {
    static let shared = APIClient()

    private init() {}

    func fetchMovies(endpoint: String) -> AnyPublisher<[Movie], Error> {
        guard let url = URL(string: "https://website.com/api/\(endpoint)") else {
            return Fail(error: URLError(.badURL)).eraseToAnyPublisher()
        }

        return URLSession.shared.dataTaskPublisher(for: url)
            .map(\.data)
            .decode(type: [Movie].self, decoder: JSONDecoder())
            .receive(on: DispatchQueue.main)
            .eraseToAnyPublisher()
    }
}

// MovieViewModel to handle business logic for movies
class MovieViewModel: ObservableObject {
    @Published var movies: [Movie] = []
    @Published var isLoading: Bool = false
    @Published var errorMessage: String?

    private var cancellables = Set<AnyCancellable>()
    private let apiClient = APIClient.shared

    // Fetch trending movies from API
    func fetchTrendingMovies() {
        isLoading = true
        apiClient.fetchMovies(endpoint: "trending/movies")
            .sink { [weak self] completion in
                self?.isLoading = false
                switch completion {
                case .failure(let error):
                    self?.errorMessage = error.localizedDescription
                case .finished:
                    break
                }
            } receiveValue: { [weak self] movies in
                self?.movies = movies
            }
            .store(in: &cancellables)
    }

    // Fetch popular movies from API
    func fetchPopularMovies() {
        isLoading = true
        apiClient.fetchMovies(endpoint: "popular/movies")
            .sink { [weak self] completion in
                self?.isLoading = false
                switch completion {
                case .failure(let error):
                    self?.errorMessage = error.localizedDescription
                case .finished:
                    break
                }
            } receiveValue: { [weak self] movies in
                self?.movies = movies
            }
            .store(in: &cancellables)
    }

    // Search movies by query
    func searchMovies(query: String) {
        isLoading = true
        apiClient.fetchMovies(endpoint: "search/movie?query=\(query)")
            .sink { [weak self] completion in
                self?.isLoading = false
                switch completion {
                case .failure(let error):
                    self?.errorMessage = error.localizedDescription
                case .finished:
                    break
                }
            } receiveValue: { [weak self] movies in
                self?.movies = movies
            }
            .store(in: &cancellables)
    }
}

// MovieDetailViewModel for movie details
class MovieDetailViewModel: ObservableObject {
    @Published var movie: Movie?
    @Published var isLoading: Bool = false
    @Published var errorMessage: String?

    private var cancellables = Set<AnyCancellable>()
    private let apiClient = APIClient.shared

    // Fetch movie details by id
    func fetchMovieDetails(movieId: Int) {
        isLoading = true
        apiClient.fetchMovies(endpoint: "movie/\(movieId)")
            .sink { [weak self] completion in
                self?.isLoading = false
                switch completion {
                case .failure(let error):
                    self?.errorMessage = error.localizedDescription
                case .finished:
                    break
                }
            } receiveValue: { [weak self] movies in
                self?.movie = movies.first
            }
            .store(in: &cancellables)
    }
}

// Usage in SwiftUI
import SwiftUI

struct MovieListView: View {
    @ObservedObject var movieViewModel = MovieViewModel()

    var body: some View {
        NavigationView {
            Group {
                if movieViewModel.isLoading {
                    ProgressView("Loading...")
                } else if let error = movieViewModel.errorMessage {
                    Text("Error: \(error)")
                } else {
                    List(movieViewModel.movies) { movie in
                        MovieRow(movie: movie)
                    }
                }
            }
            .navigationTitle("Movies")
            .onAppear {
                movieViewModel.fetchTrendingMovies()
            }
        }
    }
}

struct MovieRow: View {
    let movie: Movie

    var body: some View {
        HStack {
            VStack(alignment: .leading) {
                Text(movie.title)
                    .font(.headline)
                Text(movie.releaseDate)
                    .font(.subheadline)
                Text("Rating: \(movie.rating, specifier: "%.1f")")
                    .font(.subheadline)
            }
            Spacer()
        }
        .padding()
    }
}

// MovieDetailView for displaying detailed information about a movie
struct MovieDetailView: View {
    @ObservedObject var movieDetailViewModel = MovieDetailViewModel()

    let movieId: Int

    var body: some View {
        VStack {
            if movieDetailViewModel.isLoading {
                ProgressView("Loading...")
            } else if let error = movieDetailViewModel.errorMessage {
                Text("Error: \(error)")
            } else if let movie = movieDetailViewModel.movie {
                VStack(alignment: .leading) {
                    Text(movie.title)
                        .font(.largeTitle)
                    Text(movie.overview)
                        .font(.body)
                    Text("Release Date: \(movie.releaseDate)")
                        .font(.subheadline)
                    Text("Rating: \(movie.rating, specifier: "%.1f")")
                        .font(.subheadline)
                }
                .padding()
            }
        }
        .onAppear {
            movieDetailViewModel.fetchMovieDetails(movieId: movieId)
        }
    }
}

struct ContentView: View {
    var body: some View {
        MovieListView()
    }
}

// Preview in SwiftUI
struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}