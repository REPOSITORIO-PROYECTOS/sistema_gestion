import React, { useState } from 'react';
import {
    Card, CardContent, CardDescription, CardHeader, CardTitle
} from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
    Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea"; // Si necesitas descripción del examen
import { ClipboardCheck, Plus, ExternalLink, CalendarIcon } from 'lucide-react';
// Podrías usar un componente de calendario si lo tienes:
// import { Calendar } from "@/components/ui/calendar"
// import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
// import { format } from "date-fns"


// Helper para formatear fechas (igual que en NewsComponent)
const formatDate = (dateString:any) => {
    if (!dateString) return "N/A";
    try {
        // Puedes usar 'toLocaleDateString' o 'toLocaleString' para incluir la hora si es necesario
        return new Date(dateString).toLocaleDateString("es-ES", {
            year: "numeric", month: "short", day: "numeric"
        });
    } catch (e) {
        return "Inválida";
    }
};

// Helper para badges de estado
const getStatusBadgeVariant = (status:any) => {
    switch (status?.toLowerCase()) {
        case 'graded':
        case 'calificado':
            return 'default'; // Necesitarías definir este variant en tu tema o usar 'default'
        case 'submitted':
        case 'entregado':
            return 'secondary';
        case 'pending':
        case 'pendiente':
            return 'warning'; // Necesitarías definir este variant o usar 'outline'
        case 'missed':
        case 'no entregado':
            return 'destructive';
        default:
            return 'outline';
    }
};

export const Evaluaciones = ({ exams = [], onAddExam }:any) => {
    const [isExamDialogOpen, setIsExamDialogOpen] = useState(false);
    const [examForm, setExamForm] = useState<any>({
        title: '',
        courseId: '', // Debería ser un select o venir del contexto
        dueDate: '',
        description: '', // Opcional
    });

    const resetExamForm = () => {
        setExamForm({ title: '', courseId: '', dueDate: '', description: '' });
    };

    const handleSaveExam = () => {
        // Validar formulario
        if (!examForm.title || !examForm.courseId) {
            alert("Por favor, complete el título y seleccione un curso.");
            return;
        }
        // Llamar a la función pasada por props para manejar la lógica de guardado
        if (onAddExam) {
            onAddExam(examForm); // Envía los datos del formulario
        } else {
            console.log("Guardando examen (simulado):", examForm);
            // Aquí iría la llamada API para crear el examen
        }
        setIsExamDialogOpen(false);
        resetExamForm();
    };


    return (
        <Card className="w-full shadow-md">
            <CardHeader className="flex flex-row items-center justify-between">
                <div>
                    <CardTitle className="flex items-center text-xl">
                        <ClipboardCheck className="mr-2 h-5 w-5 text-primary" />
                        Exámenes y Calificaciones
                    </CardTitle>
                    <CardDescription>Revisa tus próximas pruebas y resultados.</CardDescription>
                </div>
                 {/* Botón para añadir examen (si se proporciona la función onAddExam) */}
                {onAddExam && (
                     <Dialog open={isExamDialogOpen} onOpenChange={setIsExamDialogOpen}>
                        <DialogTrigger asChild>
                            <Button size="sm" onClick={resetExamForm}>
                                <Plus className="mr-2 h-4 w-4" /> Nuevo Examen
                            </Button>
                        </DialogTrigger>
                        <DialogContent className="sm:max-w-[450px]">
                            <DialogHeader>
                                <DialogTitle>Nuevo Examen</DialogTitle>
                                <DialogDescription>
                                    Introduce los detalles del nuevo examen.
                                </DialogDescription>
                            </DialogHeader>
                            <div className="grid gap-4 py-4">
                                <div className="grid gap-2">
                                    <Label htmlFor="exam-title">Título del Examen</Label>
                                    <Input
                                        id="exam-title"
                                        value={examForm.title}
                                        onChange={(e) => setExamForm({...examForm, title: e.target.value})}
                                        placeholder="Ej: Examen Parcial 1"
                                    />
                                </div>
                                <div className="grid gap-2">
                                    <Label htmlFor="exam-course">Curso</Label>
                                    {/* Idealmente, esto sería un Select con los cursos disponibles */}
                                    <Input
                                        id="exam-course"
                                        value={examForm.courseId}
                                        onChange={(e) => setExamForm({...examForm, courseId: e.target.value})}
                                        placeholder="ID del curso (ej: c1)"
                                    />
                                </div>
                                <div className="grid gap-2">
                                    <Label htmlFor="exam-due-date">Fecha Límite</Label>
                                    <Input
                                        id="exam-due-date"
                                        type="date"
                                        value={examForm.dueDate}
                                        onChange={(e) => setExamForm({...examForm, dueDate: e.target.value})}
                                    />
                                     {/* Alternativa con Popover y Calendar: */}
                                     {/* <Popover> <PopoverTrigger asChild> <Button ...> <CalendarIcon/> {examForm.dueDate ? format(examForm.dueDate, "PPP") : "Pick a date"} </Button> </PopoverTrigger> <PopoverContent> <Calendar .../> </PopoverContent> </Popover> */}
                                </div>
                                 <div className="grid gap-2">
                                      <Label htmlFor="exam-desc">Descripción (Opcional)</Label>
                                      <Textarea
                                          id="exam-desc"
                                          value={examForm.description}
                                          onChange={(e) => setExamForm({...examForm, description: e.target.value})}
                                          placeholder="Instrucciones adicionales..."
                                          rows={3}
                                      />
                                  </div>
                            </div>
                            <DialogFooter>
                                <Button variant="outline" onClick={() => setIsExamDialogOpen(false)}>Cancelar</Button>
                                <Button onClick={handleSaveExam}>Guardar Examen</Button>
                            </DialogFooter>
                        </DialogContent>
                    </Dialog>
                )}
            </CardHeader>
            <CardContent>
                {exams.length > 0 ? (
                    <div className="space-y-3">
                        {exams.map((exam:any) => (
                            <Card key={exam.id} className="p-4 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3">
                                <div className="flex-grow">
                                    <p className="font-semibold">{exam.title}</p>
                                    <p className="text-sm text-muted-foreground">
                                        Curso: {exam.courseTitle || exam.courseId}
                                    </p>
                                    <p className="text-xs text-muted-foreground">
                                        Fecha Límite: {formatDate(exam.dueDate)}
                                    </p>
                                </div>
                                <div className="flex items-center space-x-2 flex-shrink-0 mt-2 sm:mt-0">
                                    <Badge variant={getStatusBadgeVariant(exam.status)}>
                                        {exam.status || 'Desconocido'}
                                    </Badge>
                                    {(exam.status?.toLowerCase() === 'graded' || exam.status?.toLowerCase() === 'calificado') && exam.grade && (
                                        <Badge variant="secondary">Nota: {exam.grade}</Badge>
                                    )}
                                    {/* Botón para ir al examen (si hay link) */}
                                    {exam.link && (
                                        <Button variant="outline" size="sm" asChild>
                                            <a href={exam.link} target="_blank" rel="noopener noreferrer">
                                                Ver Examen <ExternalLink className="ml-1 h-3 w-3" />
                                            </a>
                                        </Button>
                                    )}
                                </div>
                            </Card>
                        ))}
                    </div>
                ) : (
                     <div className="text-center py-6 text-muted-foreground">
                        No hay exámenes pendientes o calificaciones disponibles.
                    </div>
                )}
            </CardContent>
        </Card>
    );
};