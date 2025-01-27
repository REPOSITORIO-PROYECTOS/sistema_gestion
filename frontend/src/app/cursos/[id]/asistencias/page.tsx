import { TablaAsistencia } from "@/components/interfaces/tabla-asistencia"

export default function AsistenciaPage() {
    return (
        <div className="container mx-auto py-10">
            <h1 className="text-2xl font-bold mb-5">Control de asistencia diarias</h1>
            <TablaAsistencia />
        </div>
    )
}