"use client"

import { CircleAlert, Loader2Icon, Plus } from "lucide-react"
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
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "./ui/select"
import { useState } from "react"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import * as z from "zod"
import { Calendar } from "./ui/calendar"
import { Popover, PopoverContent, PopoverTrigger } from "./ui/popover"
import { cn } from "@/lib/utils"
import { format } from "date-fns"
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "./ui/form"
import MultipleSelector from "@/components/ui/multiselect";
import { useFetch } from "@/hooks/useFetch"
import { useLoading } from "@/hooks/useLoading"
import { toast } from "sonner"

const formSchema = z.object({
    name: z.string().min(2, {
        message: "El nombre debe tener al menos 2 caracteres.",
    }),
    surname: z.string().min(2, {
        message: "El apellido debe tener al menos 2 caracteres.",
    }),
    email: z.string().email({
        message: "Debe ser un email válido.",
    }),
    dni: z.string().min(7, {
        message: "El DNI debe tener al menos 7 caracteres.",
    }),
    status: z.enum(["activo", "inactivo", "pendiente"], {
        required_error: "Por favor seleccione un estado.",
    }),
    phone: z.string().min(10, {
        message: "El teléfono debe tener al menos 10 dígitos.",
    }),
    dateOfBirth: z.date({
        required_error: "La fecha de nacimiento es requerida.",
    }),
    ingressDate: z.date({
        required_error: "La fecha de ingreso es requerida.",
    }),
    cursesIds: z.array(z.string()).min(1, {
        message: "Debe seleccionar al menos un curso.",
    }),
})
const courseOptions = [
    { label: "Matemáticas", value: "math101" },
    { label: "Historia", value: "hist101" },
    { label: "Ciencias", value: "sci101" },
    { label: "Literatura", value: "lit101" },
    { label: "Programación", value: "cs101" },
]
interface AgregarPersonaProps {
    // Propiedades
    mutate: () => void
}

export default function AgregarPersona(props: AgregarPersonaProps) {
    const [open, setOpen] = useState(false)
    const { finishLoading, isLoading, startLoading } = useLoading()
    const fetch = useFetch()
    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            name: "",
            surname: "",
            email: "",
            dni: "",
            status: undefined,
            phone: "",
            dateOfBirth: undefined,
            ingressDate: undefined,
            cursesIds: [],
        },
    })

    async function onSubmit(formData: z.infer<typeof formSchema>) {
        console.log(formData)
        startLoading()
        try {
            // Envío del formulario
            const response = await fetch({
                endpoint: 'api/estudiantes/crear',
                formData
            })
            if (response) {
                console.log(response)
                await props.mutate()
                toast.success("Usuario creado correctamente.")
                setOpen(false)
            }
            return response
        } catch (error: any) {
            const errorMessage =
                error?.response?.data?.message ||
                error?.message ||
                "Error al crear el usuario. Inténtalo de nuevo."
            console.error("Error en onSubmit: ", errorMessage)
            toast.error(errorMessage)
        } finally {
            finishLoading()
        }
    }

    return (
        <div className="flex items-center gap-3">
            <AlertDialog open={open} onOpenChange={setOpen}>
                <AlertDialogTrigger asChild>
                    <Button className="ml-auto" variant="outline">
                        <Plus className="-ms-1 me-2 opacity-60" size={16} strokeWidth={2} aria-hidden="true" />
                        Agregar Usuario
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
                            <AlertDialogTitle>Formulario de inscripción</AlertDialogTitle>
                            <AlertDialogDescription>Por favor, complete todos los campos del formulario.</AlertDialogDescription>
                        </AlertDialogHeader>
                    </div>
                    <Form {...form}>
                        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4 mt-4">
                            <div className="grid grid-cols-2 gap-4">
                                <FormField
                                    control={form.control}
                                    name="name"
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>Nombre</FormLabel>
                                            <FormControl>
                                                <Input {...field} />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />
                                <FormField
                                    control={form.control}
                                    name="surname"
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>Apellido</FormLabel>
                                            <FormControl>
                                                <Input {...field} />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />
                            </div>
                            <FormField
                                control={form.control}
                                name="email"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Email</FormLabel>
                                        <FormControl>
                                            <Input type="email" {...field} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <div className="grid grid-cols-2 gap-4">
                                <FormField
                                    control={form.control}
                                    name="dni"
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>DNI</FormLabel>
                                            <FormControl>
                                                <Input {...field} />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />
                                <FormField
                                    control={form.control}
                                    name="phone"
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>Teléfono</FormLabel>
                                            <FormControl>
                                                <Input {...field} />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />
                            </div>
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
                                                <SelectItem value="activo">Activo</SelectItem>
                                                <SelectItem value="inactivo">Inactivo</SelectItem>
                                                <SelectItem value="pendiente">Pendiente</SelectItem>
                                            </SelectContent>
                                        </Select>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <div className="grid grid-cols-2 gap-4">
                                <FormField
                                    control={form.control}
                                    name="dateOfBirth"
                                    render={({ field }) => (
                                        <FormItem className="flex flex-col">
                                            <FormLabel>Fecha de Nacimiento</FormLabel>
                                            <Popover>
                                                <PopoverTrigger asChild>
                                                    <FormControl>
                                                        <Button
                                                            variant={"outline"}
                                                            className={cn(
                                                                "w-full pl-3 text-left font-normal",
                                                                !field.value && "text-muted-foreground",
                                                            )}
                                                        >
                                                            {field.value ? format(field.value, "PPP") : <span>Seleccione una fecha</span>}
                                                        </Button>
                                                    </FormControl>
                                                </PopoverTrigger>
                                                <PopoverContent className="w-auto p-0" align="start">
                                                    <Calendar
                                                        mode="single"
                                                        selected={field.value}
                                                        onSelect={field.onChange}
                                                        disabled={(date) => date > new Date() || date < new Date("1900-01-01")}
                                                        initialFocus
                                                    />
                                                </PopoverContent>
                                            </Popover>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />
                                <FormField
                                    control={form.control}
                                    name="ingressDate"
                                    render={({ field }) => (
                                        <FormItem className="flex flex-col">
                                            <FormLabel>Fecha de Ingreso</FormLabel>
                                            <Popover>
                                                <PopoverTrigger asChild>
                                                    <FormControl>
                                                        <Button
                                                            variant={"outline"}
                                                            className={cn(
                                                                "w-full pl-3 text-left font-normal",
                                                                !field.value && "text-muted-foreground",
                                                            )}
                                                        >
                                                            {field.value ? format(field.value, "PPP") : <span>Seleccione una fecha</span>}
                                                        </Button>
                                                    </FormControl>
                                                </PopoverTrigger>
                                                <PopoverContent className="w-auto p-0" align="start">
                                                    <Calendar
                                                        mode="single"
                                                        selected={field.value}
                                                        onSelect={field.onChange}
                                                        disabled={(date) => date > new Date() || date < new Date("1900-01-01")}
                                                        initialFocus
                                                    />
                                                </PopoverContent>
                                            </Popover>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />
                            </div>
                            <FormField
                                control={form.control}
                                name="cursesIds"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Cursos</FormLabel>
                                        <FormControl>
                                            <MultipleSelector
                                                options={courseOptions}
                                                //@ts-ignore
                                                selected={
                                                    field.value?.map(
                                                        (id: string) =>
                                                            courseOptions.find((option) => option.value === id) || { value: id, label: id },
                                                    ) || []
                                                }
                                                onChange={(selected) => field.onChange(selected.map((option) => option.value))}
                                                placeholder="Seleccionar cursos..."
                                            />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                        </form>
                    </Form>
                    <AlertDialogFooter>
                        <AlertDialogCancel onClick={() => setOpen(false)}>Cancelar</AlertDialogCancel>
                        <AlertDialogAction onClick={form.handleSubmit(onSubmit)}>
                            {
                                isLoading ? <Loader2Icon className="animate-spin" size={16} strokeWidth={2} /> : <Plus className="-ms-1 me-2 opacity-60" size={16} strokeWidth={2} aria-hidden="true" />
                            }
                            {
                                isLoading ? "Creando..." : "Crear"
                            }
                        </AlertDialogAction>
                    </AlertDialogFooter>
                </AlertDialogContent>
            </AlertDialog>
        </div>
    )
}
