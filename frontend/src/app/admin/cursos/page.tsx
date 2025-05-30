import TableCursos from "@/components/interfaces/table-cursos";

export default function Page() {
    return (
        <section className="container flex flex-col gap-12 mx-auto p-16">
            <div>
                <h2 className="text-primary text-2xl font-semibold">
                    PANEL DE CURSOS
                </h2>
            </div>
            <TableCursos />
        </section>
    );
}
