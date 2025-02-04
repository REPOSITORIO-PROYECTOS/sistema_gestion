"use client"

import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle, AlertDialogTrigger } from "./ui/alert-dialog"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "./ui/select"
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "./ui/form"
import { CalendarWithMonthYearPicker } from "./ui/calendar-with-month-year-picker"
import { Popover, PopoverContent, PopoverTrigger } from "./ui/popover"
import { CircleAlert, Loader2Icon, Plus } from "lucide-react"
import MultipleSelector from "@/components/ui/multiselect";
import { zodResolver } from "@hookform/resolvers/zod"
import { useLoading } from "@/hooks/useLoading"
import { cn, formatDate } from "@/lib/utils"
import { useEffect, useState } from "react"
import { useFetch } from "@/hooks/useFetch"
import { useForm } from "react-hook-form"
import { Button } from "./ui/button"
import { es } from "date-fns/locale"
import { Input } from "./ui/input"
import { format, isValid } from "date-fns"
import { toast } from "sonner"
import * as z from "zod"
import { ScopedMutator } from "swr"

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
interface EditarPersonaProps {
    mutate: ScopedMutator;
    datos: {
        id: string
        name: string
        surname: string
        email: string
        dni: string
        status: 'activo' | 'inactivo' | 'pendiente'
        phone: string
        dateOfBirth: Date
        ingressDate: Date
        cursesIds: string[]
    }
}

export default function EditarPersona(props: EditarPersonaProps) {
    const [courseOptions, setCourseOptions] = useState<{ label: string; value: string }[]>([])
    const [isLoading, setIsLoading] = useState(true)
    const [isOpen, setIsOpen] = useState(false)
    const { finishLoading, loading, startLoading } = useLoading()
    const fetch = useFetch()
    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            name: props.datos.name,
            surname: props.datos.surname,
            email: props.datos.email,
            dni: props.datos.dni,
            status: props.datos.status,
            phone: props.datos.phone,
            dateOfBirth: props.datos.dateOfBirth,
            ingressDate: props.datos.ingressDate,
            cursesIds: props.datos.cursesIds,
        },
    })

    const getCourseOptions = async () => {
        setIsLoading(true)
        try {
            const response = await fetch({
                endpoint: 'cursos/todos?page=0&size=100',
                method: 'GET',
            })
            if (response) {
                const courses = response
                if (courses) {
                    const options = courses.map((course: any) => ({ label: course.title, value: course.id }))
                    setCourseOptions(options)
                } else {
                    toast.error("Error al cargar los cursos. Inténtalo de nuevo.")
                }
            }
        } catch (error: any) {
            const errorMessage =
                (typeof error === 'object' && error.response
                    ? error.response.data?.message
                    : error?.message) ||
                "Error al cargar los cursos. Inténtalo de nuevo.";
            console.error("Error en getCourseOptions: ", errorMessage)
            toast.error(errorMessage)
        } finally {
            setIsLoading(false)
        }
    }

    useEffect(() => {
        getCourseOptions()
    }, [])

    async function onSubmit(data: z.infer<typeof formSchema>) {
        const formData = {
            name: data.name,
            surname: data.surname,
            email: data.email,
            dni: data.dni,
            status: data.status,
            phone: data.phone,
            dateOfBirth: formatDate(data.dateOfBirth),
            ingressDate: formatDate(data.ingressDate),
            cursesIds: data.cursesIds,
        }
        startLoading()
        try {
            // Envío del formulario
            const response = await fetch({
                endpoint: `estudiantes/actualizar${props.datos.id}`,
                formData
            })
            if (response) {
                console.log(response)
                await props.mutate(undefined, true)
                toast.success("Usuario creado correctamente.")
                setIsOpen(false)
                form.reset()
            }
            return response
        } catch (error: any) {
            const errorMessage =
                (typeof error === 'object' && error.response
                    ? error.response.data?.message
                    : error?.message) ||
                "Error al crear el usuario. Inténtalo de nuevo.";
            console.error("Error en onSubmit: ", errorMessage)
            toast.error(errorMessage)
        } finally {
            finishLoading()
        }
    }

    return (
        <div className="flex items-center gap-3">
            <AlertDialog open={isOpen} onOpenChange={setIsOpen}>
                <AlertDialogTrigger asChild>
                    <span>
                        Editar
                    </span>
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
                        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4 mt-4 py-6 px-2 overflow-y-auto [&::-webkit-scrollbar]:w-2 [&::-webkit-scrollbar-track]:rounded-full [&::-webkit-scrollbar-track]:bg-gray-100 [&::-webkit-scrollbar-thumb]:rounded-full [&::-webkit-scrollbar-thumb]:bg-gray-300">
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
                                    render={({ field }) => {
                                        const dateValue = new Date(field.value);
                                        const formattedDate = isValid(dateValue)
                                            ? format(dateValue, "dd/MM/yyyy")
                                            : "";
                                        return (
                                            <FormItem>
                                                <FormLabel>Fecha de nacimiento</FormLabel>
                                                <FormControl>
                                                    <Input
                                                        type="text"
                                                        value={formattedDate}
                                                        onChange={(e) => {
                                                            const inputDate = new Date(e.target.value);
                                                            if (isValid(inputDate)) {
                                                                field.onChange(inputDate);
                                                            } else {
                                                                field.onChange(e.target.value);
                                                            }
                                                        }}
                                                    />
                                                </FormControl>
                                                <FormMessage />
                                            </FormItem>
                                        );
                                    }}
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
                                                            {field.value ? format(field.value, "PPP", { locale: es }) : <span>Seleccione una fecha</span>}
                                                        </Button>
                                                    </FormControl>
                                                </PopoverTrigger>
                                                <PopoverContent className="w-auto p-0" align="start">
                                                    <CalendarWithMonthYearPicker
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
                            {
                                isLoading ? <Loader2Icon className="animate-spin" size={16} strokeWidth={2} /> : <FormField
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
                            }

                        </form>
                    </Form>
                    <AlertDialogFooter>
                        <AlertDialogCancel onClick={() => setIsOpen(false)}>Cancelar</AlertDialogCancel>
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
