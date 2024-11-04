import Foundation

struct Movie: Codable {
    let id: Int
    let title: String
    let description: String
    let releaseDate: Date
    let duration: Int // Duration in minutes
    let genre: Genre
    let rating: Double
    let director: String
    let cast: [Actor]
    let language: String
    let country: String
    let posterUrl: String
    let backdropUrl: String
    let trailerUrl: String?
    let isDownloaded: Bool
    let isFavorite: Bool

    enum Genre: String, Codable {
        case action = "Action"
        case comedy = "Comedy"
        case drama = "Drama"
        case thriller = "Thriller"
        case horror = "Horror"
        case documentary = "Documentary"
        case animation = "Animation"
    }

    struct Actor: Codable {
        let name: String
        let role: String
    }

    // Custom Decoding from API Date Strings
    enum CodingKeys: String, CodingKey {
        case id
        case title
        case description
        case releaseDate
        case duration
        case genre
        case rating
        case director
        case cast
        case language
        case country
        case posterUrl
        case backdropUrl
        case trailerUrl
        case isDownloaded
        case isFavorite
    }

    init(
        id: Int,
        title: String,
        description: String,
        releaseDate: Date,
        duration: Int,
        genre: Genre,
        rating: Double,
        director: String,
        cast: [Actor],
        language: String,
        country: String,
        posterUrl: String,
        backdropUrl: String,
        trailerUrl: String?,
        isDownloaded: Bool,
        isFavorite: Bool
    ) {
        self.id = id
        self.title = title
        self.description = description
        self.releaseDate = releaseDate
        self.duration = duration
        self.genre = genre
        self.rating = rating
        self.director = director
        self.cast = cast
        self.language = language
        self.country = country
        self.posterUrl = posterUrl
        self.backdropUrl = backdropUrl
        self.trailerUrl = trailerUrl
        self.isDownloaded = isDownloaded
        self.isFavorite = isFavorite
    }

    // Custom decoding for date format
    init(from decoder: Decoder) throws {
        let values = try decoder.container(keyedBy: CodingKeys.self)
        id = try values.decode(Int.self, forKey: .id)
        title = try values.decode(String.self, forKey: .title)
        description = try values.decode(String.self, forKey: .description)
        let releaseDateString = try values.decode(String.self, forKey: .releaseDate)
        releaseDate = Movie.dateFormatter.date(from: releaseDateString) ?? Date()
        duration = try values.decode(Int.self, forKey: .duration)
        genre = try values.decode(Genre.self, forKey: .genre)
        rating = try values.decode(Double.self, forKey: .rating)
        director = try values.decode(String.self, forKey: .director)
        cast = try values.decode([Actor].self, forKey: .cast)
        language = try values.decode(String.self, forKey: .language)
        country = try values.decode(String.self, forKey: .country)
        posterUrl = try values.decode(String.self, forKey: .posterUrl)
        backdropUrl = try values.decode(String.self, forKey: .backdropUrl)
        trailerUrl = try values.decodeIfPresent(String.self, forKey: .trailerUrl)
        isDownloaded = try values.decode(Bool.self, forKey: .isDownloaded)
        isFavorite = try values.decode(Bool.self, forKey: .isFavorite)
    }

    func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encode(id, forKey: .id)
        try container.encode(title, forKey: .title)
        try container.encode(description, forKey: .description)
        try container.encode(Movie.dateFormatter.string(from: releaseDate), forKey: .releaseDate)
        try container.encode(duration, forKey: .duration)
        try container.encode(genre.rawValue, forKey: .genre)
        try container.encode(rating, forKey: .rating)
        try container.encode(director, forKey: .director)
        try container.encode(cast, forKey: .cast)
        try container.encode(language, forKey: .language)
        try container.encode(country, forKey: .country)
        try container.encode(posterUrl, forKey: .posterUrl)
        try container.encode(backdropUrl, forKey: .backdropUrl)
        try container.encode(trailerUrl, forKey: .trailerUrl)
        try container.encode(isDownloaded, forKey: .isDownloaded)
        try container.encode(isFavorite, forKey: .isFavorite)
    }

    // Date formatter for decoding/encoding dates
    static let dateFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        return formatter
    }()

    // Computed Properties
    var isNewRelease: Bool {
        let calendar = Calendar.current
        let components = calendar.dateComponents([.year], from: releaseDate, to: Date())
        return (components.year ?? 0) <= 1
    }

    var formattedDuration: String {
        return "\(duration / 60)h \(duration % 60)m"
    }

    var shortDescription: String {
        return description.count > 100 ? String(description.prefix(100)) + "..." : description
    }

    // Utility Methods
    mutating func toggleFavorite() {
        self.isFavorite.toggle()
    }

    mutating func markAsDownloaded() {
        self.isDownloaded = true
    }

    // Fetches movie data from a local JSON file
    static func fetchMovies(from file: String) -> [Movie]? {
        guard let url = Bundle.main.url(forResource: file, withExtension: "json"),
              let data = try? Data(contentsOf: url),
              let movies = try? JSONDecoder().decode([Movie].self, from: data) else {
            return nil
        }
        return movies
    }
}

// Usage of Actor model inside Movie
struct Actor: Codable {
    let name: String
    let role: String
}

// Testing function 
func testMovieModel() {
    let sampleMovie = Movie(
        id: 1,
        title: "Sample Movie",
        description: "This is a sample movie description for testing the Movie model.",
        releaseDate: Date(),
        duration: 120,
        genre: .action,
        rating: 8.5,
        director: "Sample Director",
        cast: [Actor(name: "Person", role: "Lead")],
        language: "English",
        country: "USA",
        posterUrl: "https://website.com/poster.jpg",
        backdropUrl: "https://website.com/backdrop.jpg",
        trailerUrl: "https://website.com/trailer.mp4",
        isDownloaded: false,
        isFavorite: false
    )

    print(sampleMovie.formattedDuration)  // Using computed property
    print(sampleMovie.isNewRelease)       // Checking if it's a new release
}