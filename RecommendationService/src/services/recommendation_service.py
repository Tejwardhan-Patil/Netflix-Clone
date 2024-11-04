import logging
from repositories.recommendation_repository import RecommendationRepository
from domain.recommendation import Recommendation

class RecommendationService:
    def __init__(self, repository: RecommendationRepository):
        """
        Initialize the recommendation service with a repository.
        """
        self.repository = repository
        self.logger = logging.getLogger(__name__)

    def get_recommendations(self, user_id: str) -> list:
        """
        Retrieve personalized recommendations for a given user.
        """
        try:
            self.logger.info(f"Fetching recommendations for user ID: {user_id}")
            recommendations = self.repository.get_recommendations_by_user(user_id)
            if recommendations:
                self.logger.info(f"Found {len(recommendations)} recommendations for user ID: {user_id}")
            else:
                self.logger.info(f"No recommendations found for user ID: {user_id}")
            return recommendations
        except Exception as e:
            self.logger.error(f"Error fetching recommendations for user ID: {user_id}: {str(e)}")
            raise

    def get_popular_recommendations(self) -> list:
        """
        Retrieve popular recommendations across all users.
        """
        try:
            self.logger.info("Fetching popular recommendations")
            recommendations = self.repository.get_popular_recommendations()
            if recommendations:
                self.logger.info(f"Found {len(recommendations)} popular recommendations")
            else:
                self.logger.info("No popular recommendations found")
            return recommendations
        except Exception as e:
            self.logger.error(f"Error fetching popular recommendations: {str(e)}")
            raise

    def add_recommendation(self, recommendation: Recommendation):
        """
        Add a new recommendation for a user.
        """
        try:
            self.logger.info(f"Adding recommendation for user ID: {recommendation.user_id}, video ID: {recommendation.video_id}")
            self.repository.save_recommendation(recommendation)
            self.logger.info(f"Recommendation added successfully for user ID: {recommendation.user_id}, video ID: {recommendation.video_id}")
        except Exception as e:
            self.logger.error(f"Error adding recommendation: {str(e)}")
            raise

    def update_recommendation(self, user_id: str, video_id: str, score: float):
        """
        Update the recommendation score for a user.
        """
        try:
            self.logger.info(f"Updating recommendation for user ID: {user_id}, video ID: {video_id} with new score: {score}")
            recommendation = self.repository.get_recommendation(user_id, video_id)
            if recommendation:
                recommendation.score = score
                self.repository.update_recommendation(recommendation)
                self.logger.info(f"Recommendation updated successfully for user ID: {user_id}, video ID: {video_id}")
            else:
                self.logger.warning(f"No recommendation found for user ID: {user_id}, video ID: {video_id}")
        except Exception as e:
            self.logger.error(f"Error updating recommendation: {str(e)}")
            raise

    def delete_recommendation(self, user_id: str, video_id: str):
        """
        Delete a recommendation for a user.
        """
        try:
            self.logger.info(f"Deleting recommendation for user ID: {user_id}, video ID: {video_id}")
            self.repository.delete_recommendation(user_id, video_id)
            self.logger.info(f"Recommendation deleted successfully for user ID: {user_id}, video ID: {video_id}")
        except Exception as e:
            self.logger.error(f"Error deleting recommendation: {str(e)}")
            raise

    def get_similar_videos(self, video_id: str) -> list:
        """
        Retrieve videos similar to the given video ID.
        """
        try:
            self.logger.info(f"Fetching similar videos for video ID: {video_id}")
            similar_videos = self.repository.get_similar_videos(video_id)
            if similar_videos:
                self.logger.info(f"Found {len(similar_videos)} similar videos for video ID: {video_id}")
            else:
                self.logger.info(f"No similar videos found for video ID: {video_id}")
            return similar_videos
        except Exception as e:
            self.logger.error(f"Error fetching similar videos: {str(e)}")
            raise

    def get_recommendation_history(self, user_id: str) -> list:
        """
        Retrieve recommendation history for a given user.
        """
        try:
            self.logger.info(f"Fetching recommendation history for user ID: {user_id}")
            history = self.repository.get_recommendation_history(user_id)
            if history:
                self.logger.info(f"Found {len(history)} recommendations in history for user ID: {user_id}")
            else:
                self.logger.info(f"No recommendation history found for user ID: {user_id}")
            return history
        except Exception as e:
            self.logger.error(f"Error fetching recommendation history for user ID: {user_id}: {str(e)}")
            raise

    def clear_recommendations(self, user_id: str):
        """
        Clear all recommendations for a user.
        """
        try:
            self.logger.info(f"Clearing all recommendations for user ID: {user_id}")
            self.repository.clear_recommendations(user_id)
            self.logger.info(f"All recommendations cleared successfully for user ID: {user_id}")
        except Exception as e:
            self.logger.error(f"Error clearing recommendations for user ID: {user_id}: {str(e)}")
            raise

    def personalize_recommendations(self, user_id: str, preferences: dict) -> list:
        """
        Personalize recommendations for a user based on their preferences.
        """
        try:
            self.logger.info(f"Personalizing recommendations for user ID: {user_id} with preferences: {preferences}")
            personalized_recommendations = self.repository.get_personalized_recommendations(user_id, preferences)
            if personalized_recommendations:
                self.logger.info(f"Found {len(personalized_recommendations)} personalized recommendations for user ID: {user_id}")
            else:
                self.logger.info(f"No personalized recommendations found for user ID: {user_id}")
            return personalized_recommendations
        except Exception as e:
            self.logger.error(f"Error personalizing recommendations for user ID: {user_id}: {str(e)}")
            raise

    def recommend_based_on_watched(self, user_id: str) -> list:
        """
        Recommend videos based on a user's watch history.
        """
        try:
            self.logger.info(f"Fetching watch history-based recommendations for user ID: {user_id}")
            watched_videos = self.repository.get_watched_videos(user_id)
            if watched_videos:
                self.logger.info(f"Found {len(watched_videos)} watched videos for user ID: {user_id}")
                recommendations = self.repository.get_recommendations_based_on_watched(watched_videos)
                self.logger.info(f"Found {len(recommendations)} recommendations based on watch history for user ID: {user_id}")
            else:
                self.logger.info(f"No watched videos found for user ID: {user_id}")
                recommendations = []
            return recommendations
        except Exception as e:
            self.logger.error(f"Error fetching watch history-based recommendations for user ID: {user_id}: {str(e)}")
            raise

    def recommend_trending_videos(self, user_id: str) -> list:
        """
        Recommend trending videos for a user.
        """
        try:
            self.logger.info(f"Fetching trending recommendations for user ID: {user_id}")
            trending_videos = self.repository.get_trending_videos()
            if trending_videos:
                self.logger.info(f"Found {len(trending_videos)} trending videos")
                recommendations = self.repository.get_recommendations_based_on_trending(trending_videos, user_id)
                self.logger.info(f"Found {len(recommendations)} trending recommendations for user ID: {user_id}")
            else:
                self.logger.info("No trending videos found")
                recommendations = []
            return recommendations
        except Exception as e:
            self.logger.error(f"Error fetching trending recommendations for user ID: {user_id}: {str(e)}")
            raise

    def recommend_based_on_genre(self, user_id: str, genre: str) -> list:
        """
        Recommend videos to a user based on their preferred genre.
        """
        try:
            self.logger.info(f"Fetching genre-based recommendations for user ID: {user_id}, genre: {genre}")
            recommendations = self.repository.get_recommendations_by_genre(user_id, genre)
            if recommendations:
                self.logger.info(f"Found {len(recommendations)} genre-based recommendations for user ID: {user_id}")
            else:
                self.logger.info(f"No genre-based recommendations found for user ID: {user_id}")
            return recommendations
        except Exception as e:
            self.logger.error(f"Error fetching genre-based recommendations for user ID: {user_id}: {str(e)}")
            raise