import os

class Config:
    # Flask configuration
    SECRET_KEY = os.getenv('SECRET_KEY', 'supersecretkey')  
    DEBUG = os.getenv('DEBUG', 'False').lower() == 'true'  
    
    # Database configuration
    SQLALCHEMY_DATABASE_URI = os.getenv('DATABASE_URL', 'sqlite:///recommendations.db')  
    SQLALCHEMY_TRACK_MODIFICATIONS = False 

    # Recommendation model configuration
    RECOMMENDER_ALGORITHM = os.getenv('RECOMMENDER_ALGORITHM', 'collaborative_filtering')  
    RECOMMENDER_MODEL_PATH = os.getenv('RECOMMENDER_MODEL_PATH', './models/recommender_model.pkl')  

    # Kafka or Event Bus configuration for event-driven recommendations
    KAFKA_BOOTSTRAP_SERVERS = os.getenv('KAFKA_BOOTSTRAP_SERVERS', 'localhost:9092')  
    RECOMMENDATION_TOPIC = os.getenv('RECOMMENDATION_TOPIC', 'recommendation-events')  

    # Cache configuration 
    CACHE_TYPE = os.getenv('CACHE_TYPE', 'redis') 
    CACHE_REDIS_URL = os.getenv('CACHE_REDIS_URL', 'redis://localhost:6379/0') 

class DevelopmentConfig(Config):
    DEBUG = True
    SQLALCHEMY_DATABASE_URI = 'sqlite:///recommendations_dev.db'  

class ProductionConfig(Config):
    DEBUG = False
    SQLALCHEMY_DATABASE_URI = os.getenv('DATABASE_URL', 'postgresql://user:password@localhost/recommendations') 

config_by_name = {
    'development': DevelopmentConfig,
    'production': ProductionConfig
}