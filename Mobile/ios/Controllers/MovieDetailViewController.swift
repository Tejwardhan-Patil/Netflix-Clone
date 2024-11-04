import UIKit
import AVKit

class MovieDetailViewController: UIViewController {
    
    // UI Components
    private let scrollView = UIScrollView()
    private let contentView = UIView()
    
    private let moviePosterImageView = UIImageView()
    private let titleLabel = UILabel()
    private let genreLabel = UILabel()
    private let releaseDateLabel = UILabel()
    private let descriptionLabel = UILabel()
    private let playButton = UIButton()
    private let favoriteButton = UIButton()
    
    // Movie Data
    var movieId: Int?
    private var movie: Movie?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        if let movieId = movieId {
            fetchMovieDetails(movieId: movieId)
        }
    }
    
    // MARK: - UI Setup
    private func setupUI() {
        view.backgroundColor = .white
        setupScrollView()
        setupMoviePoster()
        setupLabels()
        setupButtons()
    }
    
    private func setupScrollView() {
        scrollView.translatesAutoresizingMaskIntoConstraints = false
        contentView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(scrollView)
        scrollView.addSubview(contentView)
        
        NSLayoutConstraint.activate([
            scrollView.topAnchor.constraint(equalTo: view.topAnchor),
            scrollView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            scrollView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            scrollView.bottomAnchor.constraint(equalTo: view.bottomAnchor),
            
            contentView.topAnchor.constraint(equalTo: scrollView.topAnchor),
            contentView.leadingAnchor.constraint(equalTo: scrollView.leadingAnchor),
            contentView.trailingAnchor.constraint(equalTo: scrollView.trailingAnchor),
            contentView.bottomAnchor.constraint(equalTo: scrollView.bottomAnchor),
            contentView.widthAnchor.constraint(equalTo: scrollView.widthAnchor)
        ])
    }
    
    private func setupMoviePoster() {
        moviePosterImageView.contentMode = .scaleAspectFill
        moviePosterImageView.translatesAutoresizingMaskIntoConstraints = false
        contentView.addSubview(moviePosterImageView)
        
        NSLayoutConstraint.activate([
            moviePosterImageView.topAnchor.constraint(equalTo: contentView.topAnchor),
            moviePosterImageView.leadingAnchor.constraint(equalTo: contentView.leadingAnchor),
            moviePosterImageView.trailingAnchor.constraint(equalTo: contentView.trailingAnchor),
            moviePosterImageView.heightAnchor.constraint(equalToConstant: 300)
        ])
    }
    
    private func setupLabels() {
        titleLabel.font = UIFont.boldSystemFont(ofSize: 24)
        titleLabel.numberOfLines = 0
        titleLabel.translatesAutoresizingMaskIntoConstraints = false
        contentView.addSubview(titleLabel)
        
        genreLabel.font = UIFont.systemFont(ofSize: 16)
        genreLabel.textColor = .gray
        genreLabel.translatesAutoresizingMaskIntoConstraints = false
        contentView.addSubview(genreLabel)
        
        releaseDateLabel.font = UIFont.systemFont(ofSize: 16)
        releaseDateLabel.textColor = .gray
        releaseDateLabel.translatesAutoresizingMaskIntoConstraints = false
        contentView.addSubview(releaseDateLabel)
        
        descriptionLabel.font = UIFont.systemFont(ofSize: 16)
        descriptionLabel.numberOfLines = 0
        descriptionLabel.translatesAutoresizingMaskIntoConstraints = false
        contentView.addSubview(descriptionLabel)
        
        NSLayoutConstraint.activate([
            titleLabel.topAnchor.constraint(equalTo: moviePosterImageView.bottomAnchor, constant: 16),
            titleLabel.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 16),
            titleLabel.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -16),
            
            genreLabel.topAnchor.constraint(equalTo: titleLabel.bottomAnchor, constant: 8),
            genreLabel.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 16),
            genreLabel.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -16),
            
            releaseDateLabel.topAnchor.constraint(equalTo: genreLabel.bottomAnchor, constant: 8),
            releaseDateLabel.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 16),
            releaseDateLabel.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -16),
            
            descriptionLabel.topAnchor.constraint(equalTo: releaseDateLabel.bottomAnchor, constant: 16),
            descriptionLabel.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 16),
            descriptionLabel.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -16)
        ])
    }
    
    private func setupButtons() {
        playButton.setTitle("Play", for: .normal)
        playButton.backgroundColor = .systemBlue
        playButton.layer.cornerRadius = 8
        playButton.translatesAutoresizingMaskIntoConstraints = false
        playButton.addTarget(self, action: #selector(playMovie), for: .touchUpInside)
        contentView.addSubview(playButton)
        
        favoriteButton.setTitle("Favorite", for: .normal)
        favoriteButton.backgroundColor = .systemRed
        favoriteButton.layer.cornerRadius = 8
        favoriteButton.translatesAutoresizingMaskIntoConstraints = false
        favoriteButton.addTarget(self, action: #selector(addToFavorites), for: .touchUpInside)
        contentView.addSubview(favoriteButton)
        
        NSLayoutConstraint.activate([
            playButton.topAnchor.constraint(equalTo: descriptionLabel.bottomAnchor, constant: 20),
            playButton.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 16),
            playButton.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -16),
            playButton.heightAnchor.constraint(equalToConstant: 50),
            
            favoriteButton.topAnchor.constraint(equalTo: playButton.bottomAnchor, constant: 16),
            favoriteButton.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 16),
            favoriteButton.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -16),
            favoriteButton.heightAnchor.constraint(equalToConstant: 50),
            favoriteButton.bottomAnchor.constraint(equalTo: contentView.bottomAnchor, constant: -20)
        ])
    }
    
    // MARK: - Networking
    private func fetchMovieDetails(movieId: Int) {
        APIClient.fetchMovieDetails(movieId: movieId) { [weak self] result in
            switch result {
            case .success(let movie):
                self?.movie = movie
                self?.updateUI(with: movie)
            case .failure(let error):
                print("Failed to load movie details: \(error.localizedDescription)")
            }
        }
    }
    
    private func updateUI(with movie: Movie) {
        DispatchQueue.main.async {
            self.titleLabel.text = movie.title
            self.genreLabel.text = movie.genre
            self.releaseDateLabel.text = "Released: \(movie.releaseDate)"
            self.descriptionLabel.text = movie.description
            if let posterURL = URL(string: movie.posterUrl) {
                self.moviePosterImageView.loadImage(from: posterURL)
            }
        }
    }
    
    // MARK: - Actions
    @objc private func playMovie() {
        guard let movie = movie else { return }
        let player = AVPlayer(url: URL(string: movie.videoUrl)!)
        let playerController = AVPlayerViewController()
        playerController.player = player
        present(playerController, animated: true) {
            player.play()
        }
    }
    
    @objc private func addToFavorites() {
        guard let movie = movie else { return }
        MovieViewModel.shared.addToFavorites(movie: movie) { success in
            if success {
                print("Movie added to favorites")
            } else {
                print("Failed to add movie to favorites")
            }
        }
    }
}