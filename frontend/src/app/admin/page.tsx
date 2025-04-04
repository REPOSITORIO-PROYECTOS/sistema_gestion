import Estadisticas from "@/components/interfaces/estadisticas";
import Panel from "@/components/interfaces/panel";
import TableEstudents from "@/components/interfaces/table-estudents";

export default function Home() {
    return (
        <>
            <Panel />
            <Estadisticas />
            <TableEstudents />
        </>
    );
}
