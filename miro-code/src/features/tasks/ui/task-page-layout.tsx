import React from "react";

export function TaskPageLayout({
  header,
  children,
  sidebar,
}: {
  header: React.ReactNode;
  children: React.ReactNode;
  sidebar?: React.ReactNode;
}) {
  return (
    <div className="container mx-auto">
      <div className="flex gap-4">
        {sidebar}
        <div className="flex-1 p-4 flex flex-col gap-6">
          {header}
          {children}
        </div>
      </div>
    </div>
  );
}

export function TaskPageLayoutHeader({
  title,
  description,
  actions,
}: {
  title: React.ReactNode;
  description?: string;
  actions?: React.ReactNode;
}) {
  return (
    <div className="flex justify-between items-center">
      <div>
        <h1 className="text-2xl font-bold">{title}</h1>
        {description && <p className="text-gray-500">{description}</p>}
      </div>

      <div className="flex gap-2">{actions}</div>
    </div>
  );
}

export function TaskPageLayoutContent({
  children,
}: {
  children: React.ReactNode;
}) {
  return <div>{children}</div>;
}
