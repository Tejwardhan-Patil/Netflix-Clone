import sys
import os

from flask import Flask, request, jsonify
from services.recommendation_service import RecommendationService
from repositories.recommendation_repository import RecommendationRepository
from domain.recommendation import Recommendation
import logging

app = Flask(__name__)

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

# Initialize logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Initialize repository and service
repository = RecommendationRepository()
service = RecommendationService(repository)

@app.route('/recommendations/<string:user_id>', methods=['GET'])
def get_recommendations(user_id):
    """
    Retrieve personalized recommendations for the given user ID.
    """
    try:
        logger.info(f"Fetching recommendations for user ID: {user_id}")
        recommendations = service.get_recommendations(user_id)
        return jsonify(recommendations), 200
    except Exception as e:
        logger.error(f"Error fetching recommendations: {str(e)}")
        return jsonify({'error': 'Unable to fetch recommendations'}), 500

@app.route('/recommendations/popular', methods=['GET'])
def get_popular_recommendations():
    """
    Retrieve popular recommendations across all users.
    """
    try:
        logger.info("Fetching popular recommendations")
        recommendations = service.get_popular_recommendations()
        return jsonify(recommendations), 200
    except Exception as e:
        logger.error(f"Error fetching popular recommendations: {str(e)}")
        return jsonify({'error': 'Unable to fetch popular recommendations'}), 500

@app.route('/recommendations/<string:user_id>', methods=['POST'])
def add_recommendation(user_id):
    """
    Add a new recommendation for a user.
    """
    try:
        data = request.get_json()
        recommendation = Recommendation(
            user_id=user_id,
            video_id=data['video_id'],
            score=data.get('score', 0),
            timestamp=data.get('timestamp', None)
        )
        logger.info(f"Adding recommendation for user ID: {user_id}, video ID: {data['video_id']}")
        service.add_recommendation(recommendation)
        return jsonify({'message': 'Recommendation added successfully'}), 201
    except Exception as e:
        logger.error(f"Error adding recommendation: {str(e)}")
        return jsonify({'error': 'Unable to add recommendation'}), 500

@app.route('/recommendations/<string:user_id>/<string:video_id>', methods=['PUT'])
def update_recommendation(user_id, video_id):
    """
    Update an existing recommendation for a user.
    """
    try:
        data = request.get_json()
        new_score = data.get('score')
        logger.info(f"Updating recommendation for user ID: {user_id}, video ID: {video_id}, new score: {new_score}")
        service.update_recommendation(user_id, video_id, new_score)
        return jsonify({'message': 'Recommendation updated successfully'}), 200
    except Exception as e:
        logger.error(f"Error updating recommendation: {str(e)}")
        return jsonify({'error': 'Unable to update recommendation'}), 500

@app.route('/recommendations/<string:user_id>/<string:video_id>', methods=['DELETE'])
def delete_recommendation(user_id, video_id):
    """
    Delete a recommendation for a user.
    """
    try:
        logger.info(f"Deleting recommendation for user ID: {user_id}, video ID: {video_id}")
        service.delete_recommendation(user_id, video_id)
        return jsonify({'message': 'Recommendation deleted successfully'}), 200
    except Exception as e:
        logger.error(f"Error deleting recommendation: {str(e)}")
        return jsonify({'error': 'Unable to delete recommendation'}), 500

@app.route('/recommendations/similar/<string:video_id>', methods=['GET'])
def get_similar_videos(video_id):
    """
    Retrieve a list of videos similar to the given video ID.
    """
    try:
        logger.info(f"Fetching similar videos for video ID: {video_id}")
        similar_videos = service.get_similar_videos(video_id)
        return jsonify(similar_videos), 200
    except Exception as e:
        logger.error(f"Error fetching similar videos: {str(e)}")
        return jsonify({'error': 'Unable to fetch similar videos'}), 500

@app.route('/recommendations/history/<string:user_id>', methods=['GET'])
def get_recommendation_history(user_id):
    """
    Retrieve recommendation history for a user.
    """
    try:
        logger.info(f"Fetching recommendation history for user ID: {user_id}")
        history = service.get_recommendation_history(user_id)
        return jsonify(history), 200
    except Exception as e:
        logger.error(f"Error fetching recommendation history: {str(e)}")
        return jsonify({'error': 'Unable to fetch recommendation history'}), 500

@app.route('/recommendations/<string:user_id>/clear', methods=['DELETE'])
def clear_user_recommendations(user_id):
    """
    Clear all recommendations for a user.
    """
    try:
        logger.info(f"Clearing recommendations for user ID: {user_id}")
        service.clear_recommendations(user_id)
        return jsonify({'message': 'Recommendations cleared successfully'}), 200
    except Exception as e:
        logger.error(f"Error clearing recommendations: {str(e)}")
        return jsonify({'error': 'Unable to clear recommendations'}), 500

@app.route('/recommendations/<string:user_id>/personalize', methods=['POST'])
def personalize_recommendations(user_id):
    """
    Personalize recommendations for a user based on their preferences.
    """
    try:
        data = request.get_json()
        preferences = data.get('preferences', {})
        logger.info(f"Personalizing recommendations for user ID: {user_id}, preferences: {preferences}")
        personalized_recommendations = service.personalize_recommendations(user_id, preferences)
        return jsonify(personalized_recommendations), 200
    except Exception as e:
        logger.error(f"Error personalizing recommendations: {str(e)}")
        return jsonify({'error': 'Unable to personalize recommendations'}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)