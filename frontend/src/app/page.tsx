import Estadisticas from "@/components/interfaces/estadisticas";
import Panel from "@/components/interfaces/panel";
import TableFilter from "@/components/interfaces/table-filter";

export default function Home() {
  return (
    <>
      <Panel />
      <Estadisticas />
      <TableFilter />
    </>
  );
}
