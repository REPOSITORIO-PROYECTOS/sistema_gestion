"use client"

import { CircleAlert, Plus } from "lucide-react"
import {
    AlertDialog,
    AlertDialogAction,
    AlertDialogCancel,
    AlertDialogContent,
    AlertDialogDescription,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogTitle,
    AlertDialogTrigger,
} from "./ui/alert-dialog"
import { Button } from "./ui/button"
import { Input } from "./ui/input"
import { Label } from "./ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "./ui/select"
import { useState } from "react"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import * as z from "zod"
import { Form, FormControl, FormDescription, FormField, FormItem, FormLabel, FormMessage } from "./ui/form"
import MultipleSelector from "@/components/ui/multiselect";
import { Textarea } from "./ui/textarea"

const formSchema = z.object({
    title: z.string().min(2, {
        message: "El título debe tener al menos 2 caracteres.",
    }),
    description: z.string().min(10, {
        message: "La descripción debe tener al menos 10 caracteres.",
    }),
    status: z.enum(["ACTIVE", "INACTIVE"], {
        required_error: "Por favor seleccione un estado.",
    }),
    monthlyPrice: z.number().positive({
        message: "El precio mensual debe ser un número positivo.",
    }),
    studentsIds: z.array(z.string()).optional(),
    teacherId: z.string().min(1, {
        message: "Debe seleccionar un profesor.",
    }),
})

// Opciones de ejemplo para los estudiantes
const studentOptions = [
    { value: "student1", label: "Estudiante 1" },
    { value: "student2", label: "Estudiante 2" },
    { value: "student3", label: "Estudiante 3" },
    { value: "student4", label: "Estudiante 4" },
    { value: "student5", label: "Estudiante 5" },
]

// Opciones de ejemplo para los profesores
const teacherOptions = [
    { value: "teacher1", label: "Profesor 1" },
    { value: "teacher2", label: "Profesor 2" },
    { value: "teacher3", label: "Profesor 3" },
]

export default function AgregarCurso() {
    const [open, setOpen] = useState(false)
    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            title: "",
            description: "",
            status: "ACTIVE",
            monthlyPrice: 0,
            studentsIds: [],
            teacherId: "",
        },
    })

    function onSubmit(values: z.infer<typeof formSchema>) {
        console.log(values)
        // Aquí puedes manejar el envío del formulario
        setOpen(false)
    }

    return (
        <div className="flex items-center gap-3">
            <AlertDialog open={open} onOpenChange={setOpen}>
                <AlertDialogTrigger asChild>
                    <Button className="ml-auto" variant="outline">
                        <Plus className="-ms-1 me-2 opacity-60" size={16} strokeWidth={2} aria-hidden="true" />
                        Agregar Curso
                    </Button>
                </AlertDialogTrigger>
                <AlertDialogContent className="sm:max-w-lg">
                    <div className="flex flex-col gap-2 max-sm:items-center sm:flex-row sm:gap-4">
                        <div
                            className="flex size-9 shrink-0 items-center justify-center rounded-full border border-border"
                            aria-hidden="true"
                        >
                            <CircleAlert className="opacity-80" size={16} strokeWidth={2} />
                        </div>
                        <AlertDialogHeader>
                            <AlertDialogTitle>Formulario de Curso</AlertDialogTitle>
                            <AlertDialogDescription>Por favor, complete todos los campos del formulario.</AlertDialogDescription>
                        </AlertDialogHeader>
                    </div>
                    <Form {...form}>
                        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4 mt-4">
                            <FormField
                                control={form.control}
                                name="title"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Título</FormLabel>
                                        <FormControl>
                                            <Input {...field} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <FormField
                                control={form.control}
                                name="description"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Descripción</FormLabel>
                                        <FormControl>
                                            <Textarea {...field} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <FormField
                                control={form.control}
                                name="status"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Estado</FormLabel>
                                        <Select onValueChange={field.onChange} defaultValue={field.value}>
                                            <FormControl>
                                                <SelectTrigger>
                                                    <SelectValue placeholder="Seleccione un estado" />
                                                </SelectTrigger>
                                            </FormControl>
                                            <SelectContent>
                                                <SelectItem value="ACTIVE">Activo</SelectItem>
                                                <SelectItem value="INACTIVE">Inactivo</SelectItem>
                                            </SelectContent>
                                        </Select>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <FormField
                                control={form.control}
                                name="monthlyPrice"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Precio Mensual</FormLabel>
                                        <FormControl>
                                            <Input
                                                type="number"
                                                {...field}
                                                onChange={(e) => field.onChange(Number.parseFloat(e.target.value))}
                                            />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <FormField
                                control={form.control}
                                name="studentsIds"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Estudiantes (opcional)</FormLabel>
                                        <FormControl>
                                            <MultipleSelector
                                                options={studentOptions}
                                                //@ts-ignore
                                                selected={
                                                    field.value?.map(
                                                        (id: string) =>
                                                            studentOptions.find((option) => option.value === id) || { value: id, label: id },
                                                    ) || []
                                                }
                                                onChange={(selected) => field.onChange(selected.map((option) => option.value))}
                                                placeholder="Seleccionar estudiantes..."
                                            />
                                        </FormControl>
                                        <FormDescription>Puede dejar este campo vacío si aún no hay estudiantes asignados.</FormDescription>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <FormField
                                control={form.control}
                                name="teacherId"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Profesor</FormLabel>
                                        <Select onValueChange={field.onChange} defaultValue={field.value}>
                                            <FormControl>
                                                <SelectTrigger>
                                                    <SelectValue placeholder="Seleccione un profesor" />
                                                </SelectTrigger>
                                            </FormControl>
                                            <SelectContent>
                                                {teacherOptions.map((teacher) => (
                                                    <SelectItem key={teacher.value} value={teacher.value}>
                                                        {teacher.label}
                                                    </SelectItem>
                                                ))}
                                            </SelectContent>
                                        </Select>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                        </form>
                    </Form>
                    <AlertDialogFooter>
                        <AlertDialogCancel onClick={() => setOpen(false)}>Cancelar</AlertDialogCancel>
                        <AlertDialogAction onClick={form.handleSubmit(onSubmit)}>Crear</AlertDialogAction>
                    </AlertDialogFooter>
                </AlertDialogContent>
            </AlertDialog>
        </div>
    )
}

