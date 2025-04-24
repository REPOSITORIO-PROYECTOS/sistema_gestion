"use client";

import { useEffect, useState } from "react";
import {
    CalendarDays,
    Clock,
    GraduationCap, // Icono más apropiado para notas
    Users, // Icono para selección de hijo
} from "lucide-react";
import { Button } from "@/components/ui/button";
import {
    Card,
    CardContent,
    CardDescription,
    CardHeader,
    CardTitle,
} from "@/components/ui/card";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select"; // Para seleccionar al hijo
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table"; // Para mostrar las notas detalladas
import { Badge } from "@/components/ui/badge";
import { useAuth } from "@/components/providers/auth-provider";

// --- Definición de Tipos para Notas ---
type Grade = {
    id: string;
    evaluationName: string; // Ej: "Examen Parcial 1", "Trabajo Práctico 2", "Nota Final"
    score: number | string; // Puede ser numérico o Aprobado/Desaprobado, etc.
    date: string; // Fecha de la evaluación o publicación
    maxScore?: number; // Opcional: Puntuación máxima posible
    comments?: string; // Opcional: Comentarios del profesor
};

type SubjectGrades = {
    id: string;
    subjectName: string;
    overallGrade?: number | string; // Nota promedio o final de la materia
    grades: Grade[]; // Lista de calificaciones individuales
    teacherName?: string; // Opcional: Nombre del profesor
};

type Child = {
    id: string;
    name: string;
};

// --- Componente ParentGradesView ---

export default function ParentGradesView() {
    const { user, isLoading: isAuthLoading } = useAuth(); // Asume que 'user' es el padre
    const [isClient, setIsClient] = useState(false);
    const [currentTime, setCurrentTime] = useState(new Date());
    const [isLoadingGrades, setIsLoadingGrades] = useState(false);
    const [children, setChildren] = useState<Child[]>([]); // Lista de hijos del padre
    const [selectedChildId, setSelectedChildId] = useState<string | null>(null);
    const [studentGrades, setStudentGrades] = useState<SubjectGrades[] | null>(
        null,
    );

    // --- Datos de Ejemplo (Reemplazar con llamadas a API) ---
    const exampleChildren: Child[] = [
        { id: "student-001", name: "Juan Pérez" },
        { id: "student-002", name: "Ana Gómez" },
    ];

    const exampleGradesData: { [key: string]: SubjectGrades[] } = {
        "student-001": [
            {
                id: "math-101",
                subjectName: "Matemáticas",
                teacherName: "Prof. López",
                overallGrade: 8.5,
                grades: [
                    { id: "m1", evaluationName: "Parcial 1", score: 7, date: "15/03/2025", maxScore: 10 },
                    { id: "m2", evaluationName: "Trabajo Práctico", score: 9, date: "05/04/2025", maxScore: 10 },
                    { id: "m3", evaluationName: "Parcial 2", score: 8, date: "10/05/2025", maxScore: 10 },
                    { id: "m4", evaluationName: "Participación", score: 10, date: "20/05/2025", maxScore: 10},
                    { id: "m5", evaluationName: "Examen Final", score: 9, date: "15/06/2025", maxScore: 10},
                ],
            },
            {
                id: "hist-101",
                subjectName: "Historia",
                teacherName: "Prof. García",
                overallGrade: "Aprobado",
                grades: [
                    { id: "h1", evaluationName: "Presentación Oral", score: "Muy Bueno", date: "20/03/2025" },
                    { id: "h2", evaluationName: "Ensayo Final", score: "Aprobado", date: "18/05/2025", comments: "Buen análisis histórico." },
                ],
            },
            {
                id: "lang-101",
                subjectName: "Lengua y Literatura",
                teacherName: "Prof. Martínez",
                overallGrade: 6.8,
                grades: [
                    { id: "l1", evaluationName: "Análisis de Texto", score: 6, date: "10/03/2025", maxScore: 10 },
                    { id: "l2", evaluationName: "Exposición", score: 7, date: "12/04/2025", maxScore: 10 },
                    { id: "l3", evaluationName: "Examen Final", score: 7, date: "10/06/2025", maxScore: 10 },
                ],
            },
        ],
        "student-002": [
            {
                id: "sci-101",
                subjectName: "Ciencias Naturales",
                teacherName: "Prof. Rodríguez",
                overallGrade: 9.2,
                grades: [
                    { id: "s1", evaluationName: "Laboratorio 1", score: 9, date: "18/03/2025", maxScore: 10 },
                    { id: "s2", evaluationName: "Examen Trimestral", score: 9.5, date: "25/04/2025", maxScore: 10 },
                    { id: "s3", evaluationName: "Proyecto de Feria", score: 9, date: "30/05/2025", maxScore: 10 },
                ],
            },
             {
                id: "art-101",
                subjectName: "Educación Artística",
                teacherName: "Prof. Díaz",
                overallGrade: "Excelente",
                grades: [
                    { id: "a1", evaluationName: "Trabajo Práctico 1", score: "Excelente", date: "01/04/2025" },
                    { id: "a2", evaluationName: "Portfolio Final", score: "Sobresaliente", date: "05/06/2025", comments: "Gran creatividad." },
                ],
            },
        ],
    };
    // --- Fin Datos de Ejemplo ---

    // Efecto para setear isClient y actualizar hora
    useEffect(() => {
        setIsClient(true);
        const timer = setInterval(() => {
            setCurrentTime(new Date());
        }, 60000); // Actualiza cada minuto

        // Simula la carga de la lista de hijos asociados al padre
        // En una app real, harías una llamada a tu API aquí
        setChildren(exampleChildren);
        // Si solo hay un hijo, seleccionarlo automáticamente
        if (exampleChildren.length === 1) {
            setSelectedChildId(exampleChildren[0].id);
        }


        return () => clearInterval(timer);
    }, []); // Se ejecuta solo una vez al montar

    // Efecto para cargar las notas cuando se selecciona un hijo
    useEffect(() => {
        if (selectedChildId) {
            setIsLoadingGrades(true);
            setStudentGrades(null); // Limpiar notas anteriores
            console.log(`Simulando carga de notas para el hijo: ${selectedChildId}`);
            // Simula una llamada a la API para obtener las notas del hijo
            setTimeout(() => {
                setStudentGrades(exampleGradesData[selectedChildId] || []); // Carga los datos de ejemplo o un array vacío si no hay
                setIsLoadingGrades(false);
                console.log("Notas cargadas.");
            }, 1000); // Simula retraso de red
        } else {
            setStudentGrades(null); // Limpiar si no hay hijo seleccionado
        }
    }, [selectedChildId]); // Se ejecuta cada vez que selectedChildId cambia

    // Estado de carga inicial (autenticación y renderizado cliente)
    if (!isClient || isAuthLoading) {
        return (
            <div className="flex h-screen items-center justify-center">
                Cargando portal de padres...
            </div>
        );
    }

    // Formatear fecha y hora
    const formattedDate = currentTime.toLocaleDateString("es-ES", {
        weekday: "long",
        year: "numeric",
        month: "long",
        day: "numeric",
    });
    const formattedTime = currentTime.toLocaleTimeString("es-ES", {
        hour: "2-digit",
        minute: "2-digit",
    });
    const capitalizedDate =
        formattedDate.charAt(0).toUpperCase() + formattedDate.slice(1);

    const handleChildChange = (value: string) => {
        setSelectedChildId(value);
    };

    // Helper para determinar el color de la nota (opcional)
    const getGradeColor = (score: number | string, maxScore?: number): string => {
        if (typeof score !== 'number' || (typeof maxScore !== 'number')) return "text-inherit"; // Color por defecto si no es numérico o no hay maxScore

        const percentage = (score / maxScore) * 100;
        if (percentage >= 90) return "text-green-600 dark:text-green-400";
        if (percentage >= 70) return "text-blue-600 dark:text-blue-400";
        if (percentage >= 50) return "text-yellow-600 dark:text-yellow-400";
        return "text-red-600 dark:text-red-500";
    };

    const selectedChildName = children.find(c => c.id === selectedChildId)?.name;

    return (
        <section className="min-h-screen flex flex-col">
            <div className="container mx-auto py-6 px-4 flex-grow">
                {/* Hero section con saludo y fecha/hora */}
                <div className="mb-8 bg-gradient-to-r from-green-50 to-teal-50 rounded-lg p-6 shadow-sm dark:from-green-600 dark:to-teal-500">
                    <div className="flex flex-col justify-between items-start gap-4 md:items-center md:flex-row">
                        <div>
                            <h1 className="text-2xl font-bold tracking-tight">
                                Portal de Padres - Bienvenido, {user?.name}
                            </h1>
                            <div className="flex items-center mt-2 text-muted-foreground dark:text-zinc-300">
                                <CalendarDays className="mr-2 h-4 w-4" />
                                <span>{capitalizedDate}</span>
                            </div>
                        </div>
                        <div className="flex items-center text-xl font-semibold">
                            <Clock className="mr-2 h-5 w-5" />
                            <span>{formattedTime}</span>
                        </div>
                    </div>
                </div>

                {/* Selección de Hijo */}
                <div className="mb-6 flex flex-col sm:flex-row items-start sm:items-center gap-4">
                     <div className="flex items-center text-lg font-semibold">
                        <Users className="mr-2 h-5 w-5" />
                        <span>Seleccione Estudiante:</span>
                    </div>
                    {children.length > 0 ? (
                         <Select onValueChange={handleChildChange} value={selectedChildId ?? undefined}>
                            <SelectTrigger className="w-full sm:w-[280px]">
                                <SelectValue placeholder="Seleccionar hijo/a..." />
                            </SelectTrigger>
                            <SelectContent>
                                {children.map((child) => (
                                    <SelectItem key={child.id} value={child.id}>
                                        {child.name}
                                    </SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                    ) : (
                         <p className="text-muted-foreground">No hay estudiantes asociados a su cuenta.</p>
                    )}
                </div>

                {/* Sección de Notas */}
                <div className="space-y-6">
                    {selectedChildId && (
                         <h2 className="text-xl font-semibold tracking-tight flex items-center">
                            <GraduationCap className="mr-2 h-5 w-5" />
                            Calificaciones de {selectedChildName || 'Estudiante Seleccionado'}
                        </h2>
                    )}

                    {isLoadingGrades && (
                        <div className="text-center py-4">Cargando calificaciones...</div>
                    )}

                    {!isLoadingGrades && !selectedChildId && (
                        <div className="text-center py-4 text-muted-foreground">
                            Por favor, seleccione un estudiante para ver sus calificaciones.
                        </div>
                    )}

                    {!isLoadingGrades && selectedChildId && (!studentGrades || studentGrades.length === 0) && (
                         <div className="text-center py-4 text-muted-foreground">
                            No hay calificaciones disponibles para {selectedChildName || 'este estudiante'} en este momento.
                        </div>
                    )}

                    {!isLoadingGrades && studentGrades && studentGrades.length > 0 && (
                        <div className="space-y-4">
                            {studentGrades.map((subject) => (
                                <Card key={subject.id}>
                                    <CardHeader>
                                        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-2">
                                             <CardTitle className="text-lg">{subject.subjectName}</CardTitle>
                                             {subject.overallGrade !== undefined && (
                                                 <Badge variant={typeof subject.overallGrade === 'number' && subject.overallGrade < 6 ? "destructive" : "secondary"}>
                                                    Nota Final: {subject.overallGrade}
                                                </Badge>
                                             )}
                                        </div>
                                         {subject.teacherName && (
                                            <CardDescription>Profesor/a: {subject.teacherName}</CardDescription>
                                         )}
                                    </CardHeader>
                                    <CardContent>
                                        {subject.grades.length > 0 ? (
                                            <Table>
                                                <TableHeader>
                                                    <TableRow>
                                                        <TableHead>Evaluación</TableHead>
                                                        <TableHead>Fecha</TableHead>
                                                        <TableHead className="text-right">Calificación</TableHead>
                                                    </TableRow>
                                                </TableHeader>
                                                <TableBody>
                                                    {subject.grades.map((grade) => (
                                                        <TableRow key={grade.id}>
                                                            <TableCell className="font-medium">
                                                                {grade.evaluationName}
                                                                {grade.comments && (
                                                                    <p className="text-xs text-muted-foreground italic mt-1">{grade.comments}</p>
                                                                )}
                                                                </TableCell>
                                                            <TableCell>{grade.date}</TableCell>
                                                            <TableCell className={`text-right font-semibold ${getGradeColor(grade.score, grade.maxScore)}`}>
                                                                {grade.score} {grade.maxScore ? `/ ${grade.maxScore}` : ''}
                                                            </TableCell>
                                                        </TableRow>
                                                    ))}
                                                </TableBody>
                                            </Table>
                                        ) : (
                                            <p className="text-sm text-muted-foreground">No hay calificaciones detalladas para esta materia todavía.</p>
                                        )}
                                    </CardContent>
                                </Card>
                            ))}
                        </div>
                    )}
                </div>
            </div>

            <footer className="border-t py-6 mt-8">
                <div className="container mx-auto px-4 text-center text-sm text-muted-foreground">
                    © {new Date().getFullYear()} Portal de Padres. Todos los derechos reservados.
                </div>
            </footer>
        </section>
    );
}