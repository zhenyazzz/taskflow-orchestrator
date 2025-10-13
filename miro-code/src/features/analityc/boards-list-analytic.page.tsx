import {
    AnalyticListLayout,
    AnalyticListLayoutContent,
    AnalyticListLayoutHeader,
} from "./analyticListLayout";
import { BoardsSidebar } from "../boards-list/ui/task/boards-sidebar";


function BoardsListPage() {
    return (
        <>
            <AnalyticListLayout
                sidebar={<BoardsSidebar />}
                header={
                    <AnalyticListLayoutHeader
                        title="Аналитика"
                        description="Здесь вы можете просматривать аналитику по задачам"
                        actions={
                            <>
                        
                            </>
                        }
                    />
                }
            >
                <AnalyticListLayoutContent
                    isEmpty={true}
                    isPending={false}
                    isPendingNext={false}
                    hasCursor={false}
                    mode="list"
                    renderList={() => null}
                    renderGrid={() => null}
                />
            </AnalyticListLayout>
        </>
    );
}

export const Component = BoardsListPage;
