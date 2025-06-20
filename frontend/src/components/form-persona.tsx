"use client";

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
} from "./ui/alert-dialog";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "./ui/select";
import {
    Form,
    FormControl,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from "./ui/form";
import { CircleAlert, Eye, EyeOff, Loader2Icon, Plus } from "lucide-react";
import MultipleSelector from "@/components/ui/multiselect";
import { zodResolver } from "@hookform/resolvers/zod";
import { parse } from "date-fns";
import { useLoading } from "@/hooks/useLoading";
import { formatDate } from "@/lib/utils";
import { useEffect, useState } from "react";
import { useFetch } from "@/hooks/useFetch";
import { useForm } from "react-hook-form";
import { Button } from "./ui/button";
import { ScopedMutator } from "swr";
import { Input } from "./ui/input";
import { toast } from "sonner";
import * as z from "zod";
import { useAuthStore } from "@/context/store";

interface PersonaFormProps {
    isEditable?: boolean;
    datos?: {
        id: string;
        name: string;
        surname: string;
        email: string;
        dni: string;
        status: "activo" | "inactivo" | "pendiente";
        phone: string;
        dateOfBirth: Date;
        ingressDate: Date;
        parentId: string[],
        cursesIds: string[];
    };
    mutate?: ScopedMutator | (() => void); // Acepta tanto ScopedMutator como una función sin argumentos
    onClose?: () => void;
}

export default function PersonaForm({
    isEditable = false,
    datos,
    mutate,
    onClose,
}: PersonaFormProps) {

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
    password: isEditable
                ? z
                    .string()
                    .optional()
                : z.string().min(6, {
                    message: "La contraseña debe tener al menos 6 caracteres.",
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
    parentId: z.array(z.string()).min(1, {
        message: "Debe seleccionar al menos un padre.",
    }).max(2, {
        message: "No puede seleccionar más de dos padres.",
    }),
    cursesIds: z.array(z.string()).min(1, {
        message: "Debe seleccionar al menos un curso.",
    }),
});

    const [courseOptions, setCourseOptions] = useState<
        { label: string; value: string }[]
    >([]);
    const [parentsOptions, setParentsOptions] = useState<{ label: string; value: string }[]>([])
    const [isLoading, setIsLoading] = useState(true);
    const [showPassword, setShowPassword] = useState(false)
    const [open, setOpen] = useState(false);
    const {user} = useAuthStore()
    const { finishLoading, loading, startLoading } = useLoading();
    const fetch = useFetch();
    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            name: datos?.name || "",
            surname: datos?.surname || "",
            email: datos?.email || "",
            dni: datos?.dni || "",
            status: datos?.status || undefined,
            phone: datos?.phone || "",
            dateOfBirth: datos?.dateOfBirth
                ? parse(datos.dateOfBirth.toString(), "dd-MM-yyyy", new Date())
                : undefined,
            ingressDate: datos?.ingressDate
                ? parse(datos.ingressDate.toString(), "dd-MM-yyyy", new Date())
                : undefined,
            parentId: datos?.parentId ? datos.parentId : [],
            cursesIds: datos?.cursesIds ?? [],
        },
    });

    useEffect(() => {
        const getCourseOptions = async () => {
            setIsLoading(true);
            try {
                const response = await fetch({
                    endpoint: "/cursos/paged?page=0&size=100",
                    method: "GET",
                    headers:{
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${user?.token}`,
                    }
                });
                if (response) {
                    const courses = response.content;
                    if (courses) {
                        const options = courses.map((course: any) => ({
                            label: course.title,
                            value: course.id,
                        }));
                        setCourseOptions(options);
                    } else {
                        toast.error(
                            "Error al cargar los cursos. Inténtalo de nuevo."
                        );
                    }
                }
            } catch (error: any) {
                const errorMessage =
                    (typeof error === "object" && error.response
                        ? error.response.data?.message
                        : error?.message) ||
                    "Error al cargar los cursos. Inténtalo de nuevo.";
                console.error("Error en getCourseOptions: ", errorMessage);
                toast.error(errorMessage);
            } finally {
                setIsLoading(false);
            }
        };

        const getParentOptions = async () => {
            setIsLoading(true)
            try {
                const response = await fetch({
                    endpoint: '/padres/paged?page=0&size=500',
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${user?.token}`
                    },
                })
                if (response) {
                    const courses = response.content
                    if (courses) {
                        const options = courses.map((course: any) => ({ label: course.name + " " + course.surname, value: course.id }))
                        setParentsOptions(options)
                    } else {
                        toast.error("Error al cargar los padres. Inténtalo de nuevo.")
                    }
                }
            } catch (error: any) {
                const errorMessage =
                    (typeof error === 'object' && error.response
                        ? error.response.data?.message
                        : error?.message) ||
                    "Error al cargar los padres. Inténtalo de nuevo.";
                console.error("Error en getParentsOptions: ", errorMessage)
                toast.error(errorMessage)
            } finally {
                setIsLoading(false)
            }
        }

        getCourseOptions();
        getParentOptions();
    }, [user?.token]);

    async function onSubmit(data: z.infer<typeof formSchema>) {
        const formData = {
            name: data.name,
            surname: data.surname,
            email: data.email,
            dni: data.dni,
            active: data.status === "activo",
            phone: data.phone,
            password: data.password,
            dateOfBirth: formatDate(data.dateOfBirth),
            ingressDate: formatDate(data.ingressDate),
            parentId: data.parentId,
            cursesIds: data.cursesIds,
            roles: ["ROLE_STUDENT"]
        };
        startLoading();
        try {
            const endpoint = isEditable
                ? `/auth/editar/${datos?.id}`
                : "/auth/registrar?userType=student";
            const response = await fetch({
                endpoint,
                method: isEditable ? "PUT" : "POST",
                headers:{
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${user?.token}`,
                },
                formData:{
                    student: formData
                },
            });
            if (response) {
                console.log(response);
                // Llamada a mutate compatible con ambos casos
                if (typeof mutate === "function") {
                    if (isEditable) {
                        await mutate(undefined, true); // Si es ScopedMutator, pasa los argumentos necesarios
                    } else {
                        await mutate(undefined, true); // Si es una función sin argumentos, llámala sin parámetros
                    }
                }
                toast.success(
                    isEditable
                        ? "Usuario actualizado correctamente."
                        : "Usuario creado correctamente."
                );
                isEditable ? onClose?.() : setOpen(false);
                form.reset();
            }
            return response;
        } catch (error: any) {
            const errorMessage =
                (typeof error === "object" && error.response
                    ? error.response.data?.message
                    : error?.message) ||
                (isEditable
                    ? "Error al actualizar el usuario. Inténtalo de nuevo."
                    : "Error al crear el usuario. Inténtalo de nuevo.");
            console.error("Error en onSubmit: ", errorMessage);
            toast.error(errorMessage);
        } finally {
            finishLoading();
        }
    }

    return (
        <div className="flex items-center gap-3">
            <AlertDialog
                open={isEditable ? true : open}
                onOpenChange={isEditable ? onClose : setOpen}
            >
                <AlertDialogTrigger asChild>
                    {isEditable ? null : (
                        <Button className="ml-auto" variant="outline">
                            <Plus
                                className="-ms-1 me-2 opacity-60"
                                size={16}
                                strokeWidth={2}
                                aria-hidden="true"
                            />
                            Agregar Estudiante
                        </Button>
                    )}
                </AlertDialogTrigger>
                <AlertDialogContent className="sm:max-w-lg">
                    <div className="flex flex-col gap-2 max-sm:items-center sm:flex-row sm:gap-4">
                        <div
                            className="flex size-9 shrink-0 items-center justify-center rounded-full border border-border"
                            aria-hidden="true"
                        >
                            <CircleAlert
                                className="opacity-80"
                                size={16}
                                strokeWidth={2}
                            />
                        </div>
                        <AlertDialogHeader>
                            <AlertDialogTitle>
                                {isEditable
                                    ? "Editar Usuario"
                                    : "Formulario de inscripción"}
                            </AlertDialogTitle>
                            <AlertDialogDescription>
                                Por favor, complete todos los campos del
                                formulario.
                            </AlertDialogDescription>
                        </AlertDialogHeader>
                    </div>
                    <Form {...form}>
                        <form
                            onSubmit={form.handleSubmit(onSubmit)}
                            className="space-y-4 mt-4 py-6 px-2 overflow-y-auto [&::-webkit-scrollbar]:w-2 [&::-webkit-scrollbar-track]:rounded-full [&::-webkit-scrollbar-track]:bg-gray-100 [&::-webkit-scrollbar-thumb]:rounded-full [&::-webkit-scrollbar-thumb]:bg-gray-300"
                        >
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
                                        <Select
                                            onValueChange={field.onChange}
                                            defaultValue={field.value}
                                        >
                                            <FormControl>
                                                <SelectTrigger>
                                                    <SelectValue placeholder="Seleccione un estado" />
                                                </SelectTrigger>
                                            </FormControl>
                                            <SelectContent>
                                                <SelectItem value="activo">
                                                    Activo
                                                </SelectItem>
                                                <SelectItem value="inactivo">
                                                    Inactivo
                                                </SelectItem>
                                                <SelectItem value="pendiente">
                                                    Pendiente
                                                </SelectItem>
                                            </SelectContent>
                                        </Select>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <FormField
                                control={isEditable ? form.control : undefined}
                                name="password"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Contraseña</FormLabel>
                                        <FormControl>
                                            <div className="relative">
                                                <Input type={showPassword ? "text" : "password"} {...field} />
                                                <Button
                                                    type="button"
                                                    variant="ghost"
                                                    size="sm"
                                                    className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent"
                                                    onClick={() => setShowPassword(!showPassword)}
                                                >
                                                    {showPassword ? (
                                                        <EyeOff className="h-4 w-4" aria-hidden="true" />
                                                    ) : (
                                                        <Eye className="h-4 w-4" aria-hidden="true" />
                                                    )}
                                                </Button>
                                            </div>
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <div className="grid grid-cols-2 gap-4">
                                <FormField
                                    control={form.control}
                                    name="dateOfBirth"
                                    render={({ field }) => {
                                        // Convertir valor a formato YYYY-MM-DD para el input date
                                        const dateValue =
                                            field.value instanceof Date
                                                ? field.value
                                                      .toISOString()
                                                      .split("T")[0]
                                                : typeof field.value ===
                                                      "string" && field.value
                                                ? parse(
                                                      field.value,
                                                      "dd-MM-yyyy",
                                                      new Date()
                                                  )
                                                      .toISOString()
                                                      .split("T")[0]
                                                : "";
                                        return (
                                            <FormItem className="flex flex-col">
                                                <FormLabel>
                                                    Fecha de Nacimiento
                                                </FormLabel>
                                                <FormControl>
                                                    <Input
                                                        type="date"
                                                        value={dateValue}
                                                        onChange={(e) => {
                                                            const date = e
                                                                .target.value
                                                                ? new Date(
                                                                      e.target.value
                                                                  )
                                                                : undefined;
                                                            field.onChange(
                                                                date
                                                            );
                                                        }}
                                                        max={
                                                            new Date()
                                                                .toISOString()
                                                                .split("T")[0]
                                                        }
                                                        min="1900-01-01"
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
                                    render={({ field }) => {
                                        // Convertir valor a formato YYYY-MM-DD para el input date
                                        const dateValue =
                                            field.value instanceof Date
                                                ? field.value
                                                      .toISOString()
                                                      .split("T")[0]
                                                : typeof field.value ===
                                                      "string" && field.value
                                                ? parse(
                                                      field.value,
                                                      "dd-MM-yyyy",
                                                      new Date()
                                                  )
                                                      .toISOString()
                                                      .split("T")[0]
                                                : "";
                                        return (
                                            <FormItem className="flex flex-col">
                                                <FormLabel>
                                                    Fecha de Ingreso
                                                </FormLabel>
                                                <FormControl>
                                                    <Input
                                                        type="date"
                                                        value={dateValue}
                                                        onChange={(e) => {
                                                            const date = e
                                                                .target.value
                                                                ? new Date(
                                                                      e.target.value
                                                                  )
                                                                : undefined;
                                                            field.onChange(
                                                                date
                                                            );
                                                        }}
                                                        max={
                                                            new Date()
                                                                .toISOString()
                                                                .split("T")[0]
                                                        }
                                                        min="1900-01-01"
                                                    />
                                                </FormControl>
                                                <FormMessage />
                                            </FormItem>
                                        );
                                    }}
                                />
                            </div>
                            {
                                isLoading ? <Loader2Icon className="animate-spin" size={16} strokeWidth={2} /> : 
                                <>
                                <FormField
                                    control={form.control}
                                    name="parentId"
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>Padres</FormLabel>
                                            <FormControl>
                                                <MultipleSelector
                                                    options={parentsOptions}
                                                    // @ts-ignore
                                                    selected={(field.value || []).map(
                                                        (id: string) =>
                                                            parentsOptions.find((option) => option.value === id) || { value: id, label: id }
                                                    )}
                                                    onChange={(selected) => field.onChange(selected.map((option) => option.value))}
                                                    placeholder="Seleccionar padre..."
                                                />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />
                                <FormField
                                    control={form.control}
                                    name="cursesIds"
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>Cursos</FormLabel>
                                            <FormControl>
                                                <MultipleSelector
                                                    options={courseOptions}
                                                    // @ts-ignore
                                                    selected={(field.value || []).map(
                                                        (id: string) =>
                                                            courseOptions.find((option) => option.value === id) || { value: id, label: id }
                                                    )}
                                                    onChange={(selected) => field.onChange(selected.map((option) => option.value))}
                                                    placeholder="Seleccionar cursos..."
                                                />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />
                                </>
                            }
                        </form>
                    </Form>
                    <AlertDialogFooter>
                        <AlertDialogCancel onClick={() => setOpen(false)}>
                            Cancelar
                        </AlertDialogCancel>
                        <AlertDialogAction
                            onClick={form.handleSubmit(onSubmit)}
                        >
                            {loading ? (
                                <Loader2Icon
                                    className="animate-spin"
                                    size={16}
                                    strokeWidth={2}
                                />
                            ) : (
                                <Plus
                                    className="-ms-1 me-2 opacity-60"
                                    size={16}
                                    strokeWidth={2}
                                    aria-hidden="true"
                                />
                            )}
                            {loading
                                ? isEditable
                                    ? "Actualizando..."
                                    : "Creando..."
                                : isEditable
                                ? "Actualizar"
                                : "Crear"}
                        </AlertDialogAction>
                    </AlertDialogFooter>
                </AlertDialogContent>
            </AlertDialog>
        </div>
    );
}
