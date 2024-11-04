import sqlite3
from typing import List, Optional, Tuple
from src.domain.recommendation import Recommendation

class RecommendationRepository:
    def __init__(self, db_path: str):
        self.db_path = db_path
        self._create_recommendations_table()

    def _create_recommendations_table(self):
        query = """
        CREATE TABLE IF NOT EXISTS recommendations (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER NOT NULL,
            video_id INTEGER NOT NULL,
            score REAL NOT NULL,
            created_at TEXT NOT NULL
        );
        """
        self._execute_query(query)

    def _execute_query(self, query: str, params: Tuple = ()):
        try:
            conn = sqlite3.connect(self.db_path)
            cursor = conn.cursor()
            cursor.execute(query, params)
            conn.commit()
            return cursor
        except sqlite3.Error as e:
            print(f"Database error: {e}")
        finally:
            conn.close()

    def _fetchall_query(self, query: str, params: Tuple = ()) -> List[Tuple]:
        try:
            conn = sqlite3.connect(self.db_path)
            cursor = conn.cursor()
            cursor.execute(query, params)
            rows = cursor.fetchall()
            return rows
        except sqlite3.Error as e:
            print(f"Database error: {e}")
            return []
        finally:
            conn.close()

    def _fetchone_query(self, query: str, params: Tuple = ()) -> Optional[Tuple]:
        try:
            conn = sqlite3.connect(self.db_path)
            cursor = conn.cursor()
            cursor.execute(query, params)
            row = cursor.fetchone()
            return row
        except sqlite3.Error as e:
            print(f"Database error: {e}")
            return None
        finally:
            conn.close()

    def add_recommendation(self, recommendation: Recommendation) -> int:
        query = """
        INSERT INTO recommendations (user_id, video_id, score, created_at)
        VALUES (?, ?, ?, ?);
        """
        params = (recommendation.user_id, recommendation.video_id, recommendation.score, recommendation.created_at)
        cursor = self._execute_query(query, params)
        return cursor.lastrowid if cursor else -1

    def get_recommendation_by_id(self, recommendation_id: int) -> Optional[Recommendation]:
        query = "SELECT * FROM recommendations WHERE id = ?;"
        row = self._fetchone_query(query, (recommendation_id,))
        if row:
            return Recommendation(id=row[0], user_id=row[1], video_id=row[2], score=row[3], created_at=row[4])
        return None

    def get_recommendations_by_user(self, user_id: int) -> List[Recommendation]:
        query = "SELECT * FROM recommendations WHERE user_id = ?;"
        rows = self._fetchall_query(query, (user_id,))
        recommendations = [Recommendation(id=row[0], user_id=row[1], video_id=row[2], score=row[3], created_at=row[4]) for row in rows]
        return recommendations

    def update_recommendation(self, recommendation_id: int, new_score: float) -> bool:
        query = "UPDATE recommendations SET score = ? WHERE id = ?;"
        params = (new_score, recommendation_id)
        self._execute_query(query, params)
        return True

    def delete_recommendation(self, recommendation_id: int) -> bool:
        query = "DELETE FROM recommendations WHERE id = ?;"
        self._execute_query(query, (recommendation_id,))
        return True

    def get_top_recommendations(self, user_id: int, limit: int = 10) -> List[Recommendation]:
        query = """
        SELECT * FROM recommendations
        WHERE user_id = ?
        ORDER BY score DESC
        LIMIT ?;
        """
        rows = self._fetchall_query(query, (user_id, limit))
        recommendations = [Recommendation(id=row[0], user_id=row[1], video_id=row[2], score=row[3], created_at=row[4]) for row in rows]
        return recommendations

    def delete_all_recommendations(self) -> None:
        query = "DELETE FROM recommendations;"
        self._execute_query(query)

    def get_recommendation_count(self) -> int:
        query = "SELECT COUNT(*) FROM recommendations;"
        row = self._fetchone_query(query)
        return row[0] if row else 0

    def get_average_score(self, user_id: int) -> Optional[float]:
        query = "SELECT AVG(score) FROM recommendations WHERE user_id = ?;"
        row = self._fetchone_query(query, (user_id,))
        return row[0] if row else None

    def get_recommendations_for_video(self, video_id: int) -> List[Recommendation]:
        query = "SELECT * FROM recommendations WHERE video_id = ?;"
        rows = self._fetchall_query(query, (video_id,))
        recommendations = [Recommendation(id=row[0], user_id=row[1], video_id=row[2], score=row[3], created_at=row[4]) for row in rows]
        return recommendations

    def bulk_insert_recommendations(self, recommendations: List[Recommendation]) -> None:
        query = """
        INSERT INTO recommendations (user_id, video_id, score, created_at)
        VALUES (?, ?, ?, ?);
        """
        params = [(rec.user_id, rec.video_id, rec.score, rec.created_at) for rec in recommendations]
        try:
            conn = sqlite3.connect(self.db_path)
            cursor = conn.cursor()
            cursor.executemany(query, params)
            conn.commit()
        except sqlite3.Error as e:
            print(f"Database error during bulk insert: {e}")
        finally:
            conn.close()

    def search_recommendations(self, search_term: str, limit: int = 10) -> List[Recommendation]:
        search_term = f"%{search_term}%"
        query = """
        SELECT * FROM recommendations
        WHERE user_id LIKE ? OR video_id LIKE ?
        LIMIT ?;
        """
        rows = self._fetchall_query(query, (search_term, search_term, limit))
        recommendations = [Recommendation(id=row[0], user_id=row[1], video_id=row[2], score=row[3], created_at=row[4]) for row in rows]
        return recommendations

    def get_recent_recommendations(self, user_id: int, limit: int = 5) -> List[Recommendation]:
        query = """
        SELECT * FROM recommendations
        WHERE user_id = ?
        ORDER BY created_at DESC
        LIMIT ?;
        """
        rows = self._fetchall_query(query, (user_id, limit))
        recommendations = [Recommendation(id=row[0], user_id=row[1], video_id=row[2], score=row[3], created_at=row[4]) for row in rows]
        return recommendations