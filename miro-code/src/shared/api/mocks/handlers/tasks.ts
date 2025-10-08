import { delay, HttpResponse } from "msw";
import { http } from "../http";
import { ApiSchemas } from "../../schema";
import { verifyTokenOrThrow } from "../session";

// Функция для генерации случайной даты в пределах последних 30 дней
function randomDate() {
  const start = new Date();
  start.setDate(start.getDate() - 30);
  const end = new Date();
  return new Date(
    start.getTime() + Math.random() * (end.getTime() - start.getTime()),
  ).toISOString();
}

// Функция для генерации случайной даты в будущем (дедлайны)
function randomFutureDate() {
  const start = new Date();
  start.setDate(start.getDate() + 1); // завтра
  const end = new Date();
  end.setDate(end.getDate() + 30); // через 30 дней
  return new Date(
    start.getTime() + Math.random() * (end.getTime() - start.getTime()),
  ).toISOString();
}

// Функция для генерации случайного названия задачи
function generateTaskTitle() {
  const prefixes = [
    "Разработать", "Внедрить", "Оптимизировать", "Исследовать", "Создать", 
    "Улучшить", "Протестировать", "Документировать", "Настроить", "Проанализировать",
    "Обновить", "Интегрировать", "Подготовить", "Запустить", "Проверить"
  ];

  const objects = [
    "новый модуль", "систему мониторинга", "API интеграцию", "пользовательский интерфейс",
    "базу данных", "отчетную систему", "процесс автоматизации", "безопасность системы",
    "производительность приложения", "систему уведомлений", "мобильное приложение",
    "веб-портал", "систему аналитики", "процесс деплоя", "документацию проекта"
  ];

  const contexts = [
    "для отдела HR", "в CRM системе", "на производстве", "в финансовом модуле",
    "для клиентского сервиса", "в системе управления", "на веб-платформе",
    "для мобильных устройств", "в корпоративной сети", "для внешних партнеров"
  ];

  const randomPrefix = prefixes[Math.floor(Math.random() * prefixes.length)];
  const randomObject = objects[Math.floor(Math.random() * objects.length)];
  const randomContext = Math.random() > 0.3 ? ` ${contexts[Math.floor(Math.random() * contexts.length)]}` : "";

  return `${randomPrefix} ${randomObject}${randomContext}`;
}

// Функция для генерации описания задачи
function generateTaskDescription() {
  const descriptions = [
    "Необходимо провести полный анализ требований и разработать техническое решение с учетом существующей архитектуры системы.",
    "Требуется внедрить новую функциональность с соблюдением стандартов кодирования и лучших практик разработки.",
    "Задача включает в себя исследование, планирование, реализацию и тестирование предложенного решения.",
    "Необходимо обеспечить совместимость с текущими системами и провести migration существующих данных.",
    "Требуется создать подробную документацию и провести обучение команды по новому функционалу.",
    "Задача предполагает работу в команде с другими разработчиками и координацию с менеджментом проекта.",
  ];
  return descriptions[Math.floor(Math.random() * descriptions.length)];
}

// Mock пользователи для назначения задач
const mockUserIds = [
  "550e8400-e29b-41d4-a716-446655440001", // admin
  "550e8400-e29b-41d4-a716-446655440002", // employee  
  "550e8400-e29b-41d4-a716-446655440003", // manager
];

// Генерация случайных задач
function generateRandomTasks(count: number): ApiSchemas["Task"][] {
  const result: ApiSchemas["Task"][] = [];
  const statuses: ApiSchemas["Task"]["status"][] = ["AVAILABLE", "ASSIGNED", "IN_PROGRESS", "REVIEW", "COMPLETED", "CANCELLED"];
  const priorities: ApiSchemas["Task"]["priority"][] = ["LOW", "MEDIUM", "HIGH", "URGENT"];
  const departments: ApiSchemas["Task"]["department"][] = ["IT", "HR", "FINANCE", "MARKETING", "SALES", "OPERATIONS", "MANAGEMENT"];

  for (let i = 0; i < count; i++) {
    const createdAt = randomDate();
    const updatedAt = new Date(
      Math.min(
        new Date(createdAt).getTime() + Math.random() * 86400000 * 10,
        new Date().getTime(),
      ),
    ).toISOString();

    const status = statuses[Math.floor(Math.random() * statuses.length)];
    const assigneeIds = status === "AVAILABLE" ? [] : 
      Math.random() > 0.5 ? [mockUserIds[Math.floor(Math.random() * mockUserIds.length)]] : [];

    result.push({
      id: crypto.randomUUID(),
      title: generateTaskTitle(),
      description: Math.random() > 0.3 ? generateTaskDescription() : undefined,
      status,
      priority: priorities[Math.floor(Math.random() * priorities.length)],
      assigneeIds,
      creatorId: mockUserIds[Math.floor(Math.random() * mockUserIds.length)],
      department: departments[Math.floor(Math.random() * departments.length)],
      createdAt,
      updatedAt,
      dueDate: Math.random() > 0.4 ? randomFutureDate() : undefined,
      tags: Math.random() > 0.6 ? 
        ["frontend", "backend", "urgent", "bug", "feature"].slice(0, Math.floor(Math.random() * 3) + 1) : 
        undefined,
      comments: [], // пустые комментарии пока
    });
  }

  return result;
}

// Создаем 1000 случайных задач
const tasks: ApiSchemas["Task"][] = generateRandomTasks(1000);

export const tasksHandlers = [
  // Получить все задачи
  http.get("/tasks", async (ctx) => {
    await verifyTokenOrThrow(ctx.request);

    const url = new URL(ctx.request.url);
    const page = Number(url.searchParams.get("page") || 0);
    const size = Number(url.searchParams.get("size") || 10);
    const search = url.searchParams.get("search");
    const status = url.searchParams.get("status");
    const department = url.searchParams.get("department");
    const assigneeId = url.searchParams.get("assigneeId");
    const creatorId = url.searchParams.get("creatorId");
    const sort = url.searchParams.get("sort") || "createdAt";

    let filteredTasks = [...tasks];

    // Фильтрация по поиску
    if (search) {
      filteredTasks = filteredTasks.filter((task) =>
        task.title.toLowerCase().includes(search.toLowerCase()) ||
        task.description?.toLowerCase().includes(search.toLowerCase())
      );
    }

    // Фильтрация по статусу
    if (status) {
      filteredTasks = filteredTasks.filter((task) => task.status === status);
    }

    // Фильтрация по отделу
    if (department) {
      filteredTasks = filteredTasks.filter((task) => task.department === department);
    }

    // Фильтрация по исполнителю
    if (assigneeId) {
      filteredTasks = filteredTasks.filter((task) => 
        task.assigneeIds && task.assigneeIds.includes(assigneeId)
      );
    }

    // Фильтрация по создателю
    if (creatorId) {
      filteredTasks = filteredTasks.filter((task) => task.creatorId === creatorId);
    }

    // Сортировка
    filteredTasks.sort((a, b) => {
      if (sort === "title") {
        return a.title.localeCompare(b.title);
      } else if (sort === "priority") {
        const priorityOrder = { LOW: 1, MEDIUM: 2, HIGH: 3, URGENT: 4 };
        return priorityOrder[b.priority!] - priorityOrder[a.priority!];
      } else {
        // Для дат (createdAt, updatedAt, dueDate)
        const aDate = a[sort as keyof ApiSchemas["Task"]] as string;
        const bDate = b[sort as keyof ApiSchemas["Task"]] as string;
        return new Date(bDate).getTime() - new Date(aDate).getTime();
      }
    });

    const totalElements = filteredTasks.length;
    const totalPages = Math.ceil(totalElements / size);
    const startIndex = page * size;
    const endIndex = startIndex + size;
    const paginatedTasks = filteredTasks.slice(startIndex, endIndex);

    await delay(500); // имитация сетевой задержки

    return HttpResponse.json({
      content: paginatedTasks,
      totalElements,
      totalPages,
      size,
      number: page,
      first: page === 0,
      last: page >= totalPages - 1,
    });
  }),

  // Получить мои задачи
  http.get("/tasks/my", async (ctx) => {
    const session = await verifyTokenOrThrow(ctx.request);
    
    const url = new URL(ctx.request.url);
    const page = Number(url.searchParams.get("page") || 0);
    const size = Number(url.searchParams.get("size") || 10);
    const status = url.searchParams.get("status");

    let filteredTasks = tasks.filter((task) => 
      task.assigneeIds && task.assigneeIds.includes(session.userId)
    );

    // Фильтрация по статусу
    if (status) {
      filteredTasks = filteredTasks.filter((task) => task.status === status);
    }

    // Сортировка по дате создания (новые сначала)
    filteredTasks.sort((a, b) => 
      new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
    );

    const totalElements = filteredTasks.length;
    const totalPages = Math.ceil(totalElements / size);
    const startIndex = page * size;
    const endIndex = startIndex + size;
    const paginatedTasks = filteredTasks.slice(startIndex, endIndex);

    await delay(300);

    return HttpResponse.json({
      content: paginatedTasks,
      totalElements,
      totalPages,
      size,
      number: page,
      first: page === 0,
      last: page >= totalPages - 1,
    });
  }),

  // Получить доступные задачи
  http.get("/tasks/available", async (ctx) => {
    await verifyTokenOrThrow(ctx.request);
    
    const url = new URL(ctx.request.url);
    const page = Number(url.searchParams.get("page") || 0);
    const size = Number(url.searchParams.get("size") || 10);
    const department = url.searchParams.get("department");

    let filteredTasks = tasks.filter((task) => task.status === "AVAILABLE");

    // Фильтрация по отделу
    if (department) {
      filteredTasks = filteredTasks.filter((task) => task.department === department);
    }

    // Сортировка по приоритету и дате создания
    filteredTasks.sort((a, b) => {
      const priorityOrder = { LOW: 1, MEDIUM: 2, HIGH: 3, URGENT: 4 };
      const priorityDiff = priorityOrder[b.priority!] - priorityOrder[a.priority!];
      if (priorityDiff !== 0) return priorityDiff;
      return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
    });

    const totalElements = filteredTasks.length;
    const totalPages = Math.ceil(totalElements / size);
    const startIndex = page * size;
    const endIndex = startIndex + size;
    const paginatedTasks = filteredTasks.slice(startIndex, endIndex);

    await delay(300);

    return HttpResponse.json({
      content: paginatedTasks,
      totalElements,
      totalPages,
      size,
      number: page,
      first: page === 0,
      last: page >= totalPages - 1,
    });
  }),

  // Получить задачу по ID
  http.get("/tasks/{taskId}", async ({ params, request }) => {
    await verifyTokenOrThrow(request);
    const { taskId } = params;
    const task = tasks.find((task) => task.id === taskId);

    if (!task) {
      return HttpResponse.json(
        { message: "Task not found", code: "NOT_FOUND" },
        { status: 404 },
      );
    }

    await delay(200);
    return HttpResponse.json(task);
  }),

  // Создать новую задачу
  http.post("/tasks", async (ctx) => {
    const session = await verifyTokenOrThrow(ctx.request);
    const body = await ctx.request.json();

    const now = new Date().toISOString();
    const task: ApiSchemas["Task"] = {
      id: crypto.randomUUID(),
      title: body.title,
      description: body.description,
      status: "AVAILABLE",
      priority: body.priority || "MEDIUM",
      assigneeIds: [],
      creatorId: session.userId,
      department: body.department || "IT",
      createdAt: now,
      updatedAt: now,
      dueDate: body.dueDate,
      tags: body.tags || [],
      comments: [],
    };

    tasks.push(task);
    await delay(500);
    return HttpResponse.json(task, { status: 201 });
  }),

  // Обновить задачу
  http.put("/tasks/{taskId}", async ({ params, request }) => {
    await verifyTokenOrThrow(request);
    const { taskId } = params;
    const task = tasks.find((task) => task.id === taskId);

    if (!task) {
      return HttpResponse.json(
        { message: "Task not found", code: "NOT_FOUND" },
        { status: 404 },
      );
    }

    const body = await request.json();
    
    // Обновляем поля
    if (body.title !== undefined) task.title = body.title;
    if (body.description !== undefined) task.description = body.description;
    if (body.status !== undefined) task.status = body.status;
    if (body.priority !== undefined) task.priority = body.priority;
    if (body.dueDate !== undefined) task.dueDate = body.dueDate;
    if (body.tags !== undefined) task.tags = body.tags;
    
    task.updatedAt = new Date().toISOString();

    await delay(400);
    return HttpResponse.json(task);
  }),

  // Удалить задачу
  http.delete("/tasks/{taskId}", async ({ params, request }) => {
    await verifyTokenOrThrow(request);
    const { taskId } = params;
    const index = tasks.findIndex((task) => task.id === taskId);

    if (index === -1) {
      return HttpResponse.json(
        { message: "Task not found", code: "NOT_FOUND" },
        { status: 404 },
      );
    }

    tasks.splice(index, 1);
    await delay(500);
    return new HttpResponse(null, { status: 204 });
  }),

  // Назначить задачу
  http.post("/tasks/{taskId}/assign", async ({ params, request }) => {
    await verifyTokenOrThrow(request);
    const { taskId } = params;
    const task = tasks.find((task) => task.id === taskId);

    if (!task) {
      return HttpResponse.json(
        { message: "Task not found", code: "NOT_FOUND" },
        { status: 404 },
      );
    }

    const body = await request.json();
    task.assigneeIds = body.assigneeIds;
    task.status = "ASSIGNED";
    task.updatedAt = new Date().toISOString();

    await delay(400);
    return HttpResponse.json(task);
  }),

  // Снять назначение задачи
  http.delete("/tasks/{taskId}/assign", async ({ params, request }) => {
    await verifyTokenOrThrow(request);
    const { taskId } = params;
    const task = tasks.find((task) => task.id === taskId);

    if (!task) {
      return HttpResponse.json(
        { message: "Task not found", code: "NOT_FOUND" },
        { status: 404 },
      );
    }

    task.assigneeIds = [];
    task.status = "AVAILABLE";
    task.updatedAt = new Date().toISOString();

    await delay(400);
    return HttpResponse.json(task);
  }),

  // Подписаться на задачу (самоназначение)
  http.post("/tasks/{taskId}/subscribe", async ({ params, request }) => {
    const session = await verifyTokenOrThrow(request);
    const { taskId } = params;
    const task = tasks.find((task) => task.id === taskId);

    if (!task) {
      return HttpResponse.json(
        { message: "Task not found", code: "NOT_FOUND" },
        { status: 404 },
      );
    }

    if (task.status !== "AVAILABLE") {
      return HttpResponse.json(
        { message: "Task is not available for subscription", code: "TASK_NOT_AVAILABLE" },
        { status: 400 },
      );
    }

    task.assigneeIds = [session.userId];
    task.status = "ASSIGNED";
    task.updatedAt = new Date().toISOString();

    await delay(400);
    return HttpResponse.json(task);
  }),
];
