import React from 'react';

const WelcomePage: React.FC = () => {
  return (
    <>
      {/* Hero Section */}
      <section className="bg-gray-200 text-black py-20">
        <div className="container mx-auto px-4 text-center">
          <h1 className="text-4xl font-bold mb-4">Добро пожаловать в нашу систему управления задачами!</h1>
          <p className="text-xl mb-8">
            Мы создали этот инструмент, чтобы помочь вашей команде работать smarter, а не harder. Здесь каждый может взять инициативу в свои руки и внести вклад в общий успех.
          </p>
          <img 
            src="https://twproject.com/blog/wp-content/uploads/pm-collaborating-with-team.png" 
            alt="Команда сотрудничает над задачами" 
            className="mx-auto rounded-lg shadow-lg max-w-md"
          />
        </div>
      </section>

      {/* Описание продукта */}
      <section className="py-16">
        <div className="container mx-auto px-4">
          <h2 className="text-3xl font-bold text-center mb-8">Для чего разработан этот продукт?</h2>
          <p className="text-lg mb-6">
            Наша система — это удобный инструмент для управления задачами внутри компании. Она сочетает в себе простоту таск-менеджеров вроде Asana или Todoist с элементами agile-методологий, чтобы ваша команда могла самоорганизовываться и достигать лучших результатов.
          </p>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            <div>
              <img 
                src="https://futuramo.com/blog/wp-content/uploads/2023/10/pexels-fauxels-3184357-800x500.jpg" 
                alt="Команда обсуждает задачи" 
                className="rounded-lg shadow-lg mb-4"
              />
              <h3 className="text-2xl font-semibold mb-2">Самоорганизация и свобода выбора</h3>
              <p>
                Сотрудники могут просматривать список доступных задач по отделам и самостоятельно "подписываться" на те, которые им подходят. Это стимулирует инициативу и помогает распределить работу равномерно.
              </p>
            </div>
            <div>
              <img 
                src="https://skillpath.com/_next/image?url=https%3A%2F%2Fspwebstorageprod.blob.core.windows.net%2Fweb-assets%2FSeamles_Teamwork_Overcoming_Collaboration_Drag_and_Team_Dysfunction_2400x1350.jpg&w=3840&q=75" 
                alt="Команда анализирует производительность" 
                className="rounded-lg shadow-lg mb-4"
              />
              <h3 className="text-2xl font-semibold mb-2">Контроль и аналитика</h3>
              <p>
                Администраторы создают задачи, назначают исполнителей и устанавливают нормы (например, N задач в неделю). Встроенная аналитика помогает отслеживать производительность команды и каждого сотрудника.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Преимущества */}
      <section className="bg-gray-200 py-16">
        <div className="container mx-auto px-4">
          <h2 className="text-3xl font-bold text-center mb-8">Почему стоит использовать нашу систему?</h2>
          <ul className="list-disc list-inside text-lg space-y-4">
            <li>Гибкость: Сочетание назначений от админов и само-подписки для сотрудников.</li>
            <li>Прозрачность: Задачи разделены по отделам, легко найти то, что нужно.</li>
            <li>Мотивация: Выполняйте нормы и видьте свой прогресс в реальном времени.</li>
            <li>Эффективность: Аналитика помогает оптимизировать работу команды.</li>
          </ul>
        </div>
      </section>
    </>
  );
};

export default WelcomePage;