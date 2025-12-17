const { MongoClient } = require('mongodb');

// --- Конфигурация MongoDB ---
const MONGO_URI = "mongodb://root:password@localhost:27017/taskdb?authSource=admin";
const DATABASE_NAME = "taskdb";

// --- Фиксированные ID пользователей (как в Java DataInitializer) ---
const ADMIN_ID = "11111111-1111-1111-1111-111111111111";
const USER_ID = "22222222-2222-2222-2222-222222222222";
const USER1_ID = "33333333-3333-3333-3333-333333333333";
const USER2_ID = "44444444-4444-4444-4444-444444444444";
const USER3_ID = "55555555-5555-5555-5555-555555555555";
const USER4_ID = "66666666-6666-6666-6666-666666666666";

const USER_IDS = [ADMIN_ID, USER_ID, USER1_ID, USER2_ID, USER3_ID, USER4_ID];
const USER_ID_TO_USERNAME = {
    [ADMIN_ID]: "admin",
    [USER_ID]: "user",
    [USER1_ID]: "user1",
    [USER2_ID]: "user2",
    [USER3_ID]: "user3",
    [USER4_ID]: "user4",
};
const USERNAMES = Object.values(USER_ID_TO_USERNAME);

// --- Вспомогательные функции и перечисления (из Java) ---
const TaskStatus = {
    AVAILABLE: "AVAILABLE",
    IN_PROGRESS: "IN_PROGRESS",
    COMPLETED: "COMPLETED",
    BLOCKED: "BLOCKED",
};

const TaskPriority = {
    LOW: "LOW",
    MEDIUM: "MEDIUM",
    HIGH: "HIGH",
};

const Department = {
    IT: "IT",
    HR: "HR",
    FINANCE: "FINANCE",
    MARKETING: "MARKETING",
    SALES: "SALES",
    CUSTOMER_SERVICE: "CUSTOMER_SERVICE",
    PRODUCTION: "PRODUCTION",
    LOGISTICS: "LOGISTICS",
    RESEARCH_AND_DEVELOPMENT: "RESEARCH_AND_DEVELOPMENT",
    OTHER: "OTHER",
};

// --- Класс для имитации TaskSeed (для удобства) ---
class TaskSeed {
    constructor(task_id, title, category, status, priority, department, assignee_ids,
                created_at, updated_at, due_date, completed_at, creator_id) {
        this.task_id = task_id;
        this.title = title;
        this.category = category;
        this.status = status;
        this.priority = priority;
        this.department = department;
        this.assignee_ids = new Set(assignee_ids);
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.due_date = due_date;
        this.completed_at = completed_at;
        this.creator_id = creator_id;
    }
}

// --- Генерация данных задач (частично взято из Java DataSeeder) ---
function buildTaskSeeds(now) {
    const seeds = [];
    const random_gen = new (require('random-js').Random)(require('random-js').MersenneTwister19937.seed(42)); // Фиксированный seed
    const categories = ["devops", "design", "migration", "testing", "monitoring", "finance", "kafka", "training", "comments", "backup", "frontend", "backend", "database", "security", "documentation", "refactoring", "optimization", "maintenance", "integration", "event", "review", "recruitment", "reporting", "audit", "budgeting", "analysis", "campaign", "website", "crm", "support", "quality", "delivery", "prototype", "patent", "meeting"];
    
    const statuses = [TaskStatus.AVAILABLE, TaskStatus.IN_PROGRESS, TaskStatus.COMPLETED, TaskStatus.BLOCKED];
    const priorities = [TaskPriority.LOW, TaskPriority.MEDIUM, TaskPriority.HIGH];
    const departments = [Department.IT, Department.HR, Department.FINANCE, Department.MARKETING, Department.SALES,
                   Department.CUSTOMER_SERVICE, Department.PRODUCTION, Department.LOGISTICS,
                   Department.RESEARCH_AND_DEVELOPMENT, Department.OTHER];

    for (let i = 1; i <= 100; i++) { // Генерируем 100 задач
        const task_id = `task-${i}`;
        const category = random_gen.pick(categories);
        const title = `Task ${i}: ${category} project`;
        const status = random_gen.pick(statuses);
        const priority = random_gen.pick(priorities);
        const department = random_gen.pick(departments);

        const assignee_ids = new Set();
        const num_assignees = random_gen.integer(1, 3); // 1 to 3 assignees
        for (let j = 0; j < num_assignees; j++) {
            assignee_ids.add(random_gen.pick(USER_IDS));
        }
        
        if (assignee_ids.size === 0) {
            assignee_ids.add(random_gen.pick(USER_IDS));
        }
        
        const creator_id = random_gen.pick(USER_IDS);

        let created_at = new Date(now.getTime() - random_gen.integer(1, 60) * 24 * 60 * 60 * 1000 - random_gen.integer(0, 23) * 60 * 60 * 1000 - random_gen.integer(0, 59) * 60 * 1000);
        let updated_at = created_at;
        let due_date = null;
        let completed_at = null;

        if (status === TaskStatus.COMPLETED) {
            updated_at = new Date(created_at.getTime() + random_gen.integer(1, 30) * 24 * 60 * 60 * 1000 + random_gen.integer(0, 23) * 60 * 60 * 1000);
            if (updated_at > now) {
                updated_at = new Date(now.getTime() - random_gen.integer(0, 5) * 60 * 60 * 1000);
            }
            completed_at = updated_at;
        } else if (status === TaskStatus.IN_PROGRESS) {
            updated_at = new Date(created_at.getTime() + random_gen.integer(1, 20) * 24 * 60 * 60 * 1000 + random_gen.integer(0, 23) * 60 * 60 * 1000);
            if (updated_at > now) {
                updated_at = new Date(now.getTime() - random_gen.integer(0, 5) * 60 * 60 * 1000);
            }
            due_date = new Date(now.getTime() + random_gen.integer(5, 30) * 24 * 60 * 60 * 1000);
        } else if (status === TaskStatus.AVAILABLE) {
            due_date = new Date(now.getTime() + random_gen.integer(10, 45) * 24 * 60 * 60 * 1000);
        } else if (status === TaskStatus.BLOCKED) {
            updated_at = new Date(created_at.getTime() + random_gen.integer(1, 10) * 24 * 60 * 60 * 1000 + random_gen.integer(0, 23) * 60 * 60 * 1000);
            if (updated_at > now) {
                updated_at = new Date(now.getTime() - random_gen.integer(0, 5) * 60 * 60 * 1000);
            }
            due_date = new Date(now.getTime() + random_gen.integer(10, 60) * 24 * 60 * 60 * 1000);
        }

        seeds.push(new TaskSeed(task_id, title, category, status, priority, department,
                              Array.from(assignee_ids), created_at, updated_at, due_date, completed_at, creator_id));
    }
    return seeds;
}

// --- Вспомогательные функции для агрегации ---
function toLocalDate(dt_obj) {
    return new Date(dt_obj.getFullYear(), dt_obj.getMonth(), dt_obj.getDate());
}

function aggregateByKey(seeds, key_extractor) {
    const counts = {};
    for (const seed of seeds) {
        const key = key_extractor(seed);
        counts[key] = (counts[key] || 0) + 1;
    }
    return counts;
}

// --- Генерация TaskDocument ---
function toTaskDocument(seed, now) {
    return {
        "_id": require('mongodb').ObjectId(), // MongoDB ObjectId
        "task_id": seed.task_id,
        "title": seed.title,
        "description_category": seed.category,
        "priority": seed.priority,
        "status": seed.status,
        "department": seed.department,
        "assignee_ids": seed.assignee_ids,
        "creator_id": seed.creator_id,
        "created_at": seed.created_at,
        "updated_at": seed.updated_at,
        "completed_at": seed.completed_at,
        "due_date": seed.due_date,
        "is_completed": seed.status === TaskStatus.COMPLETED,
        "is_deleted": false,
        "last_updated": now,
    };
}

// --- Генерация TaskStatistics за последние N дней ---
function buildTaskStatisticsForLastNDays(seeds, today, now, n_days = 30) {
    const stats_list = [];
    
    for (let days_ago = n_days; days_ago >= 0; days_ago--) {
        const date = new Date(today.getTime());
        date.setDate(today.getDate() - days_ago);
        date.setHours(0, 0, 0, 0); // Обнуляем время для сравнения только по дате

        const localDate = new Date(date.getFullYear(), date.getMonth(), date.getDate());

        const tasksCreatedByDate = seeds.filter(s => toLocalDate(s.created_at) <= localDate);
        const tasksCompletedByDate = seeds.filter(s => s.completed_at && toLocalDate(s.completed_at) <= localDate);
        const tasksInProgressByDate = seeds.filter(s => s.status === TaskStatus.IN_PROGRESS && toLocalDate(s.created_at) <= localDate && (!s.completed_at || toLocalDate(s.completed_at) > localDate));
        const tasksPendingByDate = seeds.filter(s => s.status === TaskStatus.AVAILABLE && toLocalDate(s.created_at) <= localDate);
        const tasksDeletedByDate = []; // Пока нет удаленных

        const createdToday = seeds.filter(s => toLocalDate(s.created_at).getTime() === localDate.getTime()).length;
        const completedToday = seeds.filter(s => s.completed_at && toLocalDate(s.completed_at).getTime() === localDate.getTime()).length;
        const updatedToday = seeds.filter(s => s.updated_at && toLocalDate(s.updated_at).getTime() === localDate.getTime()).length;
        const deletedToday = 0; // Пока нет удаленных

        const total_tasks_cumulative = tasksCreatedByDate.length;
        const completed_tasks_cumulative = tasksCompletedByDate.length;
        const in_progress_tasks_cumulative = tasksInProgressByDate.length;
        const pending_tasks_cumulative = tasksPendingByDate.length;
        const deleted_tasks_cumulative = tasksDeletedByDate.length;

        const completion_percentage = (total_tasks_cumulative > 0) ? (completed_tasks_cumulative * 100.0 / total_tasks_cumulative) : 0.0;

        const current_tasks_for_aggregation = seeds.filter(s => toLocalDate(s.created_at) <= localDate);

        const tasks_by_status = aggregateByKey(current_tasks_for_aggregation, s => s.status);
        const tasks_by_priority = aggregateByKey(current_tasks_for_aggregation, s => s.priority);
        const tasks_by_category = aggregateByKey(current_tasks_for_aggregation, s => s.category);
        const tasks_by_department = aggregateByKey(current_tasks_for_aggregation, s => s.department);

        const stats = {
            "_id": require('mongodb').ObjectId(), // MongoDB ObjectId
            "date": localDate,
            "total_tasks": total_tasks_cumulative,
            "completed_tasks": completed_tasks_cumulative,
            "in_progress_tasks": in_progress_tasks_cumulative,
            "pending_tasks": pending_tasks_cumulative,
            "deleted_tasks": deleted_tasks_cumulative,
            "completion_percentage": completion_percentage,
            "created_tasks_today": createdToday,
            "completed_tasks_today": completedToday,
            "deleted_tasks_today": deletedToday,
            "updated_tasks_today": updatedToday,
            "tasks_by_status": tasks_by_status,
            "tasks_by_priority": tasks_by_priority,
            "tasks_by_category": tasks_by_category,
            "tasks_by_department": tasks_by_department,
            "last_updated": new Date(now.getTime() - days_ago * 24 * 60 * 60 * 1000),
        };
        stats_list.push(stats);
    }
    return stats_list;
}

// --- Генерация UserStatistics за последние N дней ---
function buildUserStatisticsForLastNDays(today, now, total_users, n_days = 30) {
    const stats_list = [];
    const random_gen = new (require('random-js').Random)(require('random-js').MersenneTwister19937.seed(42));
    
    for (let days_ago = n_days; days_ago >= 0; days_ago--) {
        const date = new Date(today.getTime());
        date.setDate(today.getDate() - days_ago);
        date.setHours(0, 0, 0, 0);
        
        const is_weekend = date.getDay() === 0 || date.getDay() === 6; // 0=Sunday, 6=Saturday
        const base_successful_logins = is_weekend ? random_gen.integer(5, 15) : random_gen.integer(20, 50);
        const base_failed_logins = random_gen.integer(0, Math.floor(base_successful_logins / 10) + 1);
        
        const recency_factor = 1.0 + (n_days - days_ago) / n_days * 0.5;
        const successful_logins = Math.round(base_successful_logins * recency_factor);
        const failed_logins = Math.round(base_failed_logins * recency_factor);
        
        const active_users_today = Math.min(total_users, Math.max(2, Math.round(successful_logins / 2.5)));
        
        const new_users_today = (days_ago >= n_days - 2) ? random_gen.integer(0, 2) : 0;

        const stats = {
            "_id": require('mongodb').ObjectId(), // MongoDB ObjectId
            "date": date,
            "total_users": total_users,
            "new_users_today": new_users_today,
            "active_users_today": active_users_today,
            "successful_logins": successful_logins,
            "failed_logins": failed_logins,
            "last_updated": new Date(now.getTime() - days_ago * 24 * 60 * 60 * 1000),
        };
        stats_list.push(stats);
    }
    return stats_list;
}

// --- Генерация DailyActiveUser за последние N дней ---
function buildDailyActiveUsersForLastNDays(today, n_days = 30) {
    const daily_active_users = [];
    const random_gen = new (require('random-js').Random)(require('random-js').MersenneTwister19937.seed(42));
    
    for (const user_id in USER_ID_TO_USERNAME) {
        const username = USER_ID_TO_USERNAME[user_id];
        
        const base_login_count = (username === "admin") ? 3 : 1;
        
        for (let days_ago = n_days; days_ago >= 0; days_ago--) {
            const date = new Date(today.getTime());
            date.setDate(today.getDate() - days_ago);
            date.setHours(0, 0, 0, 0);
            
            const is_weekend = date.getDay() === 0 || date.getDay() === 6;
            const login_probability = (username === "admin") ? 0.8 : (is_weekend ? 0.4 : 0.6);
            
            if (random_gen.realZeroToOneInclusive() < login_probability) {
                let login_count = base_login_count + random_gen.integer(0, 4);
                const recency_factor = 1.0 + (n_days - days_ago) / n_days * 0.3;
                login_count = Math.round(login_count * recency_factor);
                
                const daily_user = {
                    "_id": require('mongodb').ObjectId(), // MongoDB ObjectId
                    "username": username,
                    "date": date,
                    "login_count": login_count,
                };
                daily_active_users.push(daily_user);
            }
        }
    }
    return daily_active_users;
}

// --- Генерация UserTaskStatistics (для каждого пользователя по задачам) ---
function toUserTaskStats(user_id, all_seeds, today, now) {
    const user_seeds = all_seeds.filter(s => s.assignee_ids.has(user_id) || s.creator_id === user_id);
    
    if (user_seeds.length === 0) {
        return null;
    }

    const total = user_seeds.length;
    const completed = user_seeds.filter(s => s.status === TaskStatus.COMPLETED).length;
    const in_progress = user_seeds.filter(s => s.status === TaskStatus.IN_PROGRESS).length;
    const pending = user_seeds.filter(s => s.status === TaskStatus.AVAILABLE).length;
    const deleted = 0; // Пока всегда 0

    const completion_percentage = (total > 0) ? (completed * 100.0 / total) : 0.0;

    const tasks_by_category = aggregateByKey(user_seeds, s => s.category);
    const tasks_by_priority = aggregateByKey(user_seeds, s => s.priority);
    const tasks_by_status = aggregateByKey(user_seeds, s => s.status);
    const tasks_by_department = aggregateByKey(user_seeds, s => s.department);

    return {
        "_id": require('mongodb').ObjectId(), // MongoDB ObjectId
        "user_id": user_id,
        "date": today, // Эти данные обычно агрегируются за текущий день
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
    };
}

// --- Основная функция заполнения БД ---
async function seedDatabase() {
    const client = new MongoClient(MONGO_URI);

    try {
        await client.connect();
        const db = client.db(DATABASE_NAME);

        console.log(`Подключение к MongoDB: ${MONGO_URI}`);

        // Очистка существующих коллекций перед заполнением
        const collectionsToClear = [
            "task_documents", "task_statistics", "user_statistics",
            "daily_active_users", "user_task_statistics", "task_counters", "user_counters"
        ];
        for (const collectionName of collectionsToClear) {
            await db.collection(collectionName).deleteMany({});
            console.log(`Очищена коллекция: ${collectionName}`);
        }

        const now = new Date();
        const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());

        // 1. Генерируем базовые задачи
        const taskSeeds = buildTaskSeeds(now);
        const taskDocumentsData = taskSeeds.map(seed => toTaskDocument(seed, now));
        if (taskDocumentsData.length > 0) {
            await db.collection("task_documents").insertMany(taskDocumentsData);
            console.log(`Вставлено ${taskDocumentsData.length} документов в 'task_documents'.`);
        } else {
            console.log("Нет данных для вставки в 'task_documents'.");
        }

        // 2. Генерируем TaskStatistics
        const taskStatisticsData = buildTaskStatisticsForLastNDays(taskSeeds, today, now);
        if (taskStatisticsData.length > 0) {
            await db.collection("task_statistics").insertMany(taskStatisticsData);
            console.log(`Вставлено ${taskStatisticsData.length} документов в 'task_statistics'.`);
        } else {
            console.log("Нет данных для вставки в 'task_statistics'.");
        }
        
        // 3. Генерируем UserStatistics
        const totalUsers = USER_IDS.length;
        const userStatisticsData = buildUserStatisticsForLastNDays(today, now, totalUsers);
        if (userStatisticsData.length > 0) {
            await db.collection("user_statistics").insertMany(userStatisticsData);
            console.log(`Вставлено ${userStatisticsData.length} документов в 'user_statistics'.`);
        } else {
            console.log("Нет данных для вставки в 'user_statistics'.");
        }

        // 4. Генерируем DailyActiveUser
        const dailyActiveUsersData = buildDailyActiveUsersForLastNDays(today);
        if (dailyActiveUsersData.length > 0) {
            await db.collection("daily_active_users").insertMany(dailyActiveUsersData);
            console.log(`Вставлено ${dailyActiveUsersData.length} документов в 'daily_active_users'.`);
        } else {
            console.log("Нет данных для вставки в 'daily_active_users'.");
        }

        // 5. Генерируем UserTaskStatistics
        const userTaskStatisticsData = [];
        for (const user_id of USER_IDS) {
            const stats = toUserTaskStats(user_id, taskSeeds, today, now);
            if (stats) {
                userTaskStatisticsData.push(stats);
            }
        }
        if (userTaskStatisticsData.length > 0) {
            await db.collection("user_task_statistics").insertMany(userTaskStatisticsData);
            console.log(`Вставлено ${userTaskStatisticsData.length} документов в 'user_task_statistics'.`);
        } else {
            console.log("Нет данных для вставки в 'user_task_statistics'.");
        }

        // 6. Генерируем TaskCounter
        const totalTasksOverall = taskStatisticsData.reduce((sum, s) => sum + s.total_tasks, 0);
        const taskCounterData = {
            "_id": "global",
            "total_tasks": totalTasksOverall,
            "last_updated": now,
        };
        await db.collection("task_counters").insertOne(taskCounterData);
        console.log("Вставлен документ в 'task_counters'.");

        // 7. Генерируем UserCounter
        const userCounterData = {
            "_id": "global",
            "total_users": totalUsers,
            "last_updated": now,
        };
        await db.collection("user_counters").insertOne(userCounterData);
        console.log("Вставлен документ в 'user_counters'.");

        console.log("База данных успешно заполнена.");

    } catch (error) {
        console.error("Ошибка при заполнении базы данных:", error);
    } finally {
        await client.close();
        console.log("Соединение с MongoDB закрыто.");
    }
}

seedDatabase();

