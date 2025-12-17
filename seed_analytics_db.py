import pymongo
from datetime import datetime, timedelta, timezone
import random
import uuid
from collections import defaultdict

# --- Конфигурация MongoDB ---
MONGO_URI = "mongodb://root:password@localhost:27017/taskdb?authSource=admin"
DATABASE_NAME = "taskdb"

# --- Фиксированные ID пользователей (как в Java DataInitializer) ---
ADMIN_ID = "11111111-1111-1111-1111-111111111111"
USER_ID = "22222222-2222-2222-2222-222222222222"
USER1_ID = "33333333-3333-3333-3333-333333333333"
USER2_ID = "44444444-4444-4444-4444-444444444444"
USER3_ID = "55555555-5555-5555-5555-555555555555"
USER4_ID = "66666666-6666-6666-6666-666666666666"

USER_IDS = [ADMIN_ID, USER_ID, USER1_ID, USER2_ID, USER3_ID, USER4_ID]
USER_ID_TO_USERNAME = {
    ADMIN_ID: "admin",
    USER_ID: "user",
    USER1_ID: "user1",
    USER2_ID: "user2",
    USER3_ID: "user3",
    USER4_ID: "user4",
}
USERNAMES = list(USER_ID_TO_USERNAME.values())

# --- Вспомогательные функции и перечисления (из Java) ---
class TaskStatus:
    AVAILABLE = "AVAILABLE"
    IN_PROGRESS = "IN_PROGRESS"
    COMPLETED = "COMPLETED"
    BLOCKED = "BLOCKED"

class TaskPriority:
    LOW = "LOW"
    MEDIUM = "MEDIUM"
    HIGH = "HIGH"

class Department:
    IT = "IT"
    HR = "HR"
    FINANCE = "FINANCE"
    MARKETING = "MARKETING"
    SALES = "SALES"
    CUSTOMER_SERVICE = "CUSTOMER_SERVICE"
    PRODUCTION = "PRODUCTION"
    LOGISTICS = "LOGISTICS"
    RESEARCH_AND_DEVELOPMENT = "RESEARCH_AND_DEVELOPMENT"
    OTHER = "OTHER"

# --- Класс для имитации TaskSeed (для удобства) ---
class TaskSeed:
    def __init__(self, task_id, title, category, status, priority, department, assignee_ids,
                 created_at, updated_at, due_date, completed_at, creator_id):
        self.task_id = task_id
        self.title = title
        self.category = category
        self.status = status
        self.priority = priority
        self.department = department
        self.assignee_ids = set(assignee_ids)
        self.created_at = created_at
        self.updated_at = updated_at
        self.due_date = due_date
        self.completed_at = completed_at
        self.creator_id = creator_id # Добавляем creator_id

# --- Генерация данных задач (частично взято из Java DataSeeder) ---
def build_task_seeds(now):
    seeds = []
    random_gen = random.Random(42) # Фиксированный seed для воспроизводимости
    categories = ["devops", "design", "migration", "testing", "monitoring", "finance", "kafka", "training", "comments", "backup", "frontend", "backend", "database", "security", "documentation", "refactoring", "optimization", "maintenance", "integration", "event", "review", "recruitment", "reporting", "audit", "budgeting", "analysis", "campaign", "website", "crm", "support", "quality", "delivery", "prototype", "patent", "meeting"]
    
    statuses = [TaskStatus.AVAILABLE, TaskStatus.IN_PROGRESS, TaskStatus.COMPLETED, TaskStatus.BLOCKED]
    priorities = [TaskPriority.LOW, TaskPriority.MEDIUM, TaskPriority.HIGH]
    departments = [Department.IT, Department.HR, Department.FINANCE, Department.MARKETING, Department.SALES,
                   Department.CUSTOMER_SERVICE, Department.PRODUCTION, Department.LOGISTICS,
                   Department.RESEARCH_AND_DEVELOPMENT, Department.OTHER]

    for i in range(1, 101): # Генерируем 100 задач
        task_id = f"task-{i}"
        category = random_gen.choice(categories)
        title = f"Task {i}: {category} project"
        status = random_gen.choice(statuses)
        priority = random_gen.choice(priorities)
        department = random_gen.choice(departments)

        assignee_ids = set()
        num_assignees = random_gen.randint(1, 3) # 1 to 3 assignees
        for _ in range(num_assignees):
            assignee_ids.add(random_gen.choice(USER_IDS))
        
        # Убедимся, что у каждой задачи есть хотя бы один исполнитель
        if not assignee_ids:
            assignee_ids.add(random_gen.choice(USER_IDS))
        
        # Creator ID для каждой задачи
        creator_id = random_gen.choice(USER_IDS)

        created_at = now - timedelta(days=random_gen.randint(1, 60), hours=random_gen.randint(0, 23), minutes=random_gen.randint(0, 59))
        updated_at = created_at
        due_date = None
        completed_at = None

        if status == TaskStatus.COMPLETED:
            updated_at = created_at + timedelta(days=random_gen.randint(1, 30), hours=random_gen.randint(0, 23))
            if updated_at > now: # Не завершать в будущем
                updated_at = now - timedelta(hours=random_gen.randint(0, 5))
            completed_at = updated_at
        elif status == TaskStatus.IN_PROGRESS:
            updated_at = created_at + timedelta(days=random_gen.randint(1, 20), hours=random_gen.randint(0, 23))
            if updated_at > now:
                updated_at = now - timedelta(hours=random_gen.randint(0, 5))
            due_date = now + timedelta(days=random_gen.randint(5, 30))
        elif status == TaskStatus.AVAILABLE:
            due_date = now + timedelta(days=random_gen.randint(10, 45))
        elif status == TaskStatus.BLOCKED:
            updated_at = created_at + timedelta(days=random_gen.randint(1, 10), hours=random_gen.randint(0, 23))
            if updated_at > now:
                updated_at = now - timedelta(hours=random_gen.randint(0, 5))
            due_date = now + timedelta(days=random_gen.randint(10, 60))

        seeds.append(TaskSeed(task_id, title, category, status, priority, department,
                              assignee_ids, created_at, updated_at, due_date, completed_at, creator_id))
    return seeds

# --- Вспомогательные функции для агрегации ---
def to_local_date(dt_obj):
    return dt_obj.date()

def aggregate_by_key(seeds, key_extractor):
    counts = defaultdict(int)
    for seed in seeds:
        key = key_extractor(seed)
        counts[key] += 1
    return dict(counts)

# --- Генерация TaskDocument ---
def to_task_document(seed, now):
    return {
        "id": str(uuid.uuid4()), # Генерируем новый ID документа
        "task_id": seed.task_id,
        "title": seed.title,
        "description_category": seed.category,
        "priority": seed.priority,
        "status": seed.status,
        "department": seed.department,
        "assignee_ids": list(seed.assignee_ids),
        "creator_id": seed.creator_id, # Используем creator_id
        "created_at": seed.created_at,
        "updated_at": seed.updated_at,
        "completed_at": seed.completed_at,
        "due_date": seed.due_date,
        "is_completed": seed.status == TaskStatus.COMPLETED,
        "is_deleted": False, # Для сидера пока всегда False
        "last_updated": now,
    }

# --- Генерация TaskStatistics за последние 30 дней ---
def build_task_statistics_for_last_n_days(seeds, today, now, n_days=30):
    stats_list = []
    
    for days_ago in range(n_days, -1, -1):
        date = today - timedelta(days=days_ago)

        tasks_created_by_date = [s for s in seeds if to_local_date(s.created_at) <= date]
        tasks_completed_by_date = [s for s in seeds if s.completed_at and to_local_date(s.completed_at) <= date]
        tasks_in_progress_by_date = [s for s in seeds if s.status == TaskStatus.IN_PROGRESS and to_local_date(s.created_at) <= date and (not s.completed_at or to_local_date(s.completed_at) > date)]
        tasks_pending_by_date = [s for s in seeds if s.status == TaskStatus.AVAILABLE and to_local_date(s.created_at) <= date]
        tasks_deleted_by_date = [s for s in seeds if False] # Пока нет удаленных

        created_today = sum(1 for s in seeds if to_local_date(s.created_at) == date)
        completed_today = sum(1 for s in seeds if s.completed_at and to_local_date(s.completed_at) == date)
        updated_today = sum(1 for s in seeds if s.updated_at and to_local_date(s.updated_at) == date)
        deleted_today = sum(1 for s in seeds if False) # Пока нет удаленных

        total_tasks_cumulative = len(tasks_created_by_date)
        completed_tasks_cumulative = len(tasks_completed_by_date)
        in_progress_tasks_cumulative = len(tasks_in_progress_by_date)
        pending_tasks_cumulative = len(tasks_pending_by_date)
        deleted_tasks_cumulative = len(tasks_deleted_by_date)

        completion_percentage = (completed_tasks_cumulative * 100.0 / total_tasks_cumulative) if total_tasks_cumulative > 0 else 0.0

        # Агрегации на основе задач, существующих на эту дату
        current_tasks = [s for s in seeds if to_local_date(s.created_at) <= date]

        tasks_by_status = aggregate_by_key(current_tasks, lambda s: s.status)
        tasks_by_priority = aggregate_by_key(current_tasks, lambda s: s.priority)
        tasks_by_category = aggregate_by_key(current_tasks, lambda s: s.category)
        tasks_by_department = aggregate_by_key(current_tasks, lambda s: s.department)

        stats = {
            "id": str(uuid.uuid4()), # Генерируем новый ID документа
            "date": date,
            "total_tasks": total_tasks_cumulative,
            "completed_tasks": completed_tasks_cumulative,
            "in_progress_tasks": in_progress_tasks_cumulative,
            "pending_tasks": pending_tasks_cumulative,
            "deleted_tasks": deleted_tasks_cumulative,
            "completion_percentage": completion_percentage,
            "created_tasks_today": created_today,
            "completed_tasks_today": completed_today,
            "deleted_tasks_today": deleted_today,
            "updated_tasks_today": updated_today,
            "tasks_by_status": tasks_by_status,
            "tasks_by_priority": tasks_by_priority,
            "tasks_by_category": tasks_by_category,
            "tasks_by_department": tasks_by_department,
            "last_updated": now - timedelta(days=days_ago),
        }
        stats_list.append(stats)
    return stats_list

# --- Генерация UserStatistics за последние 30 дней ---
def build_user_statistics_for_last_n_days(today, now, total_users, n_days=30):
    stats_list = []
    random_gen = random.Random(42) # Фиксированный seed
    
    for days_ago in range(n_days, -1, -1):
        date = today - timedelta(days=days_ago)
        
        is_weekend = date.weekday() >= 5 # 5=Saturday, 6=Sunday
        base_successful_logins = random_gen.randint(5, 15) if is_weekend else random_gen.randint(20, 50)
        base_failed_logins = random_gen.randint(0, base_successful_logins // 10 + 1)
        
        recency_factor = 1.0 + (n_days - days_ago) / n_days * 0.5
        successful_logins = round(base_successful_logins * recency_factor)
        failed_logins = round(base_failed_logins * recency_factor)
        
        active_users_today = min(total_users, max(2, round(successful_logins / 2.5)))
        
        new_users_today = random_gen.randint(0, 2) if days_ago >= n_days - 2 else 0 # Новые пользователи в первые 3 дня

        stats = {
            "id": str(uuid.uuid4()), # Генерируем новый ID документа
            "date": date,
            "total_users": total_users,
            "new_users_today": new_users_today,
            "active_users_today": active_users_today,
            "successful_logins": successful_logins,
            "failed_logins": failed_logins,
            "last_updated": now - timedelta(days=days_ago),
        }
        stats_list.append(stats)
    return stats_list

# --- Генерация DailyActiveUser за последние 30 дней ---
def build_daily_active_users_for_last_n_days(today, n_days=30):
    daily_active_users = []
    random_gen = random.Random(42) # Фиксированный seed
    
    for user_id, username in USER_ID_TO_USERNAME.items():
        base_login_count = 3 if username == "admin" else 1
        
        for days_ago in range(n_days, -1, -1):
            date = today - timedelta(days=days_ago)
            
            is_weekend = date.weekday() >= 5
            login_probability = 0.8 if username == "admin" else (0.4 if is_weekend else 0.6)
            
            if random_gen.random() < login_probability:
                login_count = base_login_count + random_gen.randint(0, 4)
                recency_factor = 1.0 + (n_days - days_ago) / n_days * 0.3
                login_count = round(login_count * recency_factor)
                
                daily_user = {
                    "id": str(uuid.uuid4()), # Генерируем новый ID документа
                    "username": username,
                    "date": date,
                    "login_count": login_count,
                }
                daily_active_users.append(daily_user)
    return daily_active_users

# --- Генерация UserTaskStatistics (для каждого пользователя по задачам) ---
def to_user_task_stats(user_id, all_seeds, today, now):
    user_seeds = [s for s in all_seeds if user_id in s.assignee_ids or s.creator_id == user_id]
    
    if not user_seeds:
        return None

    total = len(user_seeds)
    completed = sum(1 for s in user_seeds if s.status == TaskStatus.COMPLETED)
    in_progress = sum(1 for s in user_seeds if s.status == TaskStatus.IN_PROGRESS)
    pending = sum(1 for s in user_seeds if s.status == TaskStatus.AVAILABLE)
    deleted = sum(1 for s in user_seeds if False) # Пока всегда 0

    completion_percentage = (completed * 100.0 / total) if total > 0 else 0.0

    tasks_by_category = aggregate_by_key(user_seeds, lambda s: s.category)
    tasks_by_priority = aggregate_by_key(user_seeds, lambda s: s.priority)
    tasks_by_status = aggregate_by_key(user_seeds, lambda s: s.status)
    tasks_by_department = aggregate_by_key(user_seeds, lambda s: s.department)

    return {
        "id": str(uuid.uuid4()), # Генерируем новый ID документа
        "user_id": user_id,
        "date": today, # Эти данные обычно агрегируются за текущий день
        "total_tasks": total,
        "completed_tasks": completed,
        "in_progress_tasks": in_progress,
        "pending_tasks": pending,
        "deleted_tasks": deleted,
        "completion_percentage": completion_percentage,
        "tasks_by_category": tasks_by_category,
        "tasks_by_priority": tasks_by_priority,
        "tasks_by_status": tasks_by_status,
        "tasks_by_department": tasks_by_department,
        "last_updated": now,
    }

# --- Основная функция заполнения БД ---
def seed_database():
    client = pymongo.MongoClient(MONGO_URI)
    db = client[DATABASE_NAME]

    print(f"Подключение к MongoDB: {MONGO_URI}")

    # Очистка существующих коллекций перед заполнением
    collections_to_clear = [
        "task_documents", "task_statistics", "user_statistics",
        "daily_active_users", "user_task_statistics", "task_counters", "user_counters"
    ]
    for collection_name in collections_to_clear:
        db[collection_name].delete_many({})
        print(f"Очищена коллекция: {collection_name}")

    now = datetime.now(timezone.utc)
    today = now.date()

    # 1. Генерируем базовые задачи
    task_seeds = build_task_seeds(now)
    task_documents_data = [to_task_document(seed, now) for seed in task_seeds]
    if task_documents_data:
        db.task_documents.insert_many(task_documents_data)
        print(f"Вставлено {len(task_documents_data)} документов в 'task_documents'.")
    else:
        print("Нет данных для вставки в 'task_documents'.")

    # 2. Генерируем TaskStatistics
    task_statistics_data = build_task_statistics_for_last_n_days(task_seeds, today, now)
    if task_statistics_data:
        db.task_statistics.insert_many(task_statistics_data)
        print(f"Вставлено {len(task_statistics_data)} документов в 'task_statistics'.")
    else:
        print("Нет данных для вставки в 'task_statistics'.")
    
    # 3. Генерируем UserStatistics
    total_users = len(USER_IDS)
    user_statistics_data = build_user_statistics_for_last_n_days(today, now, total_users)
    if user_statistics_data:
        db.user_statistics.insert_many(user_statistics_data)
        print(f"Вставлено {len(user_statistics_data)} документов в 'user_statistics'.")
    else:
        print("Нет данных для вставки в 'user_statistics'.")

    # 4. Генерируем DailyActiveUser
    daily_active_users_data = build_daily_active_users_for_last_n_days(today)
    if daily_active_users_data:
        db.daily_active_users.insert_many(daily_active_users_data)
        print(f"Вставлено {len(daily_active_users_data)} документов в 'daily_active_users'.")
    else:
        print("Нет данных для вставки в 'daily_active_users'.")

    # 5. Генерируем UserTaskStatistics
    user_task_statistics_data = []
    for user_id in USER_IDS:
        stats = to_user_task_stats(user_id, task_seeds, today, now)
        if stats:
            user_task_statistics_data.append(stats)
    if user_task_statistics_data:
        db.user_task_statistics.insert_many(user_task_statistics_data)
        print(f"Вставлено {len(user_task_statistics_data)} документов в 'user_task_statistics'.")
    else:
        print("Нет данных для вставки в 'user_task_statistics'.")

    # 6. Генерируем TaskCounter
    total_tasks_overall = sum(s['total_tasks'] for s in task_statistics_data) if task_statistics_data else 0
    task_counter_data = {
        "id": "global",
        "total_tasks": total_tasks_overall,
        "last_updated": now,
    }
    db.task_counters.insert_one(task_counter_data)
    print("Вставлен документ в 'task_counters'.")

    # 7. Генерируем UserCounter
    user_counter_data = {
        "id": "global",
        "total_users": total_users,
        "last_updated": now,
    }
    db.user_counters.insert_one(user_counter_data)
    print("Вставлен документ в 'user_counters'.")

    client.close()
    print("База данных успешно заполнена.")

if __name__ == "__main__":
    seed_database()

