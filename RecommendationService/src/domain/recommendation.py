import numpy as np
import pandas as pd
from sklearn.metrics.pairwise import cosine_similarity
from collections import defaultdict

class RecommendationEngine:
    def __init__(self, user_data_path: str, video_data_path: str):
        self.user_data = pd.read_csv(user_data_path)
        self.video_data = pd.read_csv(video_data_path)
        self.user_similarity = None
        self.video_similarity = None
        self.user_preferences = defaultdict(list)
        self.load_user_preferences()

    def load_user_preferences(self):
        for index, row in self.user_data.iterrows():
            user_id = row['user_id']
            video_id = row['video_id']
            self.user_preferences[user_id].append(video_id)

    def calculate_user_similarity(self):
        # Create user-video matrix
        user_video_matrix = pd.pivot_table(self.user_data, values='rating', index='user_id', columns='video_id').fillna(0)
        self.user_similarity = cosine_similarity(user_video_matrix)
        return self.user_similarity

    def calculate_video_similarity(self):
        # Create video-feature matrix
        video_feature_matrix = pd.get_dummies(self.video_data[['genre', 'director', 'cast']])
        self.video_similarity = cosine_similarity(video_feature_matrix)
        return self.video_similarity

    def recommend_for_user(self, user_id: int, num_recommendations: int = 5):
        if user_id not in self.user_preferences:
            raise ValueError(f"User {user_id} not found")

        watched_videos = set(self.user_preferences[user_id])
        similarity_scores = self.user_similarity[user_id]
        # Get similar users based on similarity score
        similar_users = np.argsort(similarity_scores)[::-1]

        recommendations = []
        for similar_user in similar_users:
            if len(recommendations) >= num_recommendations:
                break
            similar_user_videos = self.user_preferences[similar_user]
            for video in similar_user_videos:
                if video not in watched_videos and video not in recommendations:
                    recommendations.append(video)
                    if len(recommendations) >= num_recommendations:
                        break
        return recommendations

    def recommend_based_on_content(self, video_id: int, num_recommendations: int = 5):
        if video_id not in self.video_data['video_id'].values:
            raise ValueError(f"Video {video_id} not found")

        video_index = self.video_data[self.video_data['video_id'] == video_id].index[0]
        similarity_scores = self.video_similarity[video_index]

        similar_videos = np.argsort(similarity_scores)[::-1]
        recommended_videos = [self.video_data.iloc[i]['video_id'] for i in similar_videos[:num_recommendations]]
        return recommended_videos

    def hybrid_recommendation(self, user_id: int, num_recommendations: int = 5):
        if user_id not in self.user_preferences:
            raise ValueError(f"User {user_id} not found")

        watched_videos = set(self.user_preferences[user_id])
        similarity_scores = self.user_similarity[user_id]
        similar_users = np.argsort(similarity_scores)[::-1]

        hybrid_recommendations = []
        for similar_user in similar_users:
            if len(hybrid_recommendations) >= num_recommendations:
                break

            similar_user_videos = self.user_preferences[similar_user]
            for video in similar_user_videos:
                if video not in watched_videos and video not in hybrid_recommendations:
                    content_based_videos = self.recommend_based_on_content(video, num_recommendations=1)
                    hybrid_recommendations.extend(content_based_videos)
                    if len(hybrid_recommendations) >= num_recommendations:
                        break
        return hybrid_recommendations[:num_recommendations]

    def evaluate_recommendations(self, user_id: int, recommended_videos: list):
        if user_id not in self.user_preferences:
            raise ValueError(f"User {user_id} not found")

        watched_videos = set(self.user_preferences[user_id])
        hits = 0
        for video in recommended_videos:
            if video in watched_videos:
                hits += 1
        return hits / len(recommended_videos) if recommended_videos else 0

if __name__ == "__main__":
    # Paths to the user and video data
    user_data_path = "/user_data.csv"
    video_data_path = "/video_data.csv"

    # Initialize recommendation engine
    recommendation_engine = RecommendationEngine(user_data_path, video_data_path)

    # Calculate similarities
    recommendation_engine.calculate_user_similarity()
    recommendation_engine.calculate_video_similarity()

    # Get recommendations for a user
    user_id = 1
    user_recommendations = recommendation_engine.recommend_for_user(user_id, num_recommendations=5)
    print(f"Recommendations for User {user_id}: {user_recommendations}")

    # Get content-based recommendations for a video
    video_id = 101
    content_recommendations = recommendation_engine.recommend_based_on_content(video_id, num_recommendations=5)
    print(f"Content-based Recommendations for Video {video_id}: {content_recommendations}")

    # Hybrid recommendations
    hybrid_recommendations = recommendation_engine.hybrid_recommendation(user_id, num_recommendations=5)
    print(f"Hybrid Recommendations for User {user_id}: {hybrid_recommendations}")

    # Evaluate recommendations
    evaluation_score = recommendation_engine.evaluate_recommendations(user_id, user_recommendations)
    print(f"Evaluation Score for User {user_id}: {evaluation_score}")