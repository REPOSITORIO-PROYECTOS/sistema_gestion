"use client"

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
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "./ui/select"
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "./ui/form"
import { CircleAlert, Loader2Icon, Plus, Eye, EyeOff } from "lucide-react"
import { zodResolver } from "@hookform/resolvers/zod"
import { useLoading } from "@/hooks/useLoading"
import { useEffect, useState } from "react"
import { useFetch } from "@/hooks/useFetch"
import { useForm } from "react-hook-form"
import { Button } from "./ui/button"
import type { ScopedMutator } from "swr"
import { Input } from "./ui/input"
import { toast } from "sonner"
import * as z from "zod"
import { useAuthStore } from "@/context/store"
import MultipleSelector from "./ui/multiselect"

interface PersonaFormProps {
    isEditable?: boolean
    datos?: {
        id?: string
        name: string
        surname: string
        email: string
        dni: string
        phone: string
        password?: string
        institution: string
        roles: ('ROLE_ADMIN' | 'ROLE_CASHER' | 'ROLE_ADMIN_VC' | 'ROLE_ADMIN_USERS' | 
            'ROLE_ADMIN_COURSES' | "ROLE_PARENT" | "ROLE_TEACHER" | undefined)[];
    }
    mutate: ScopedMutator | (() => void)
    onClose?: any
}

export default function UserFormNoAdmin({ isEditable = false, datos, mutate, onClose }: PersonaFormProps) {
    const formSchema = z.object({
        id: z.string().optional(),
        name: z.string().optional(),
        surname: z.string().optional(),
        email: z.string().optional(),
        dni: z.string().optional(),
        phone: z.string().optional(),
        password: z.string().optional(),
        roles: z.array(z.enum(["ROLE_ADMIN", "ROLE_CASHER", "ROLE_ADMIN_VC", 
            "ROLE_ADMIN_USERS", "ROLE_ADMIN_COURSES", "ROLE_PARENT", "ROLE_TEACHER"])).optional(),
    })
    const { user } = useAuthStore();
    const [open, setOpen] = useState(false)
    const [actualUserData, setActualUserData] = useState<any>(undefined)
    const [showPassword, setShowPassword] = useState(false)
    const { finishLoading, loading, startLoading } = useLoading()
    const fetch = useFetch()
    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            id: datos?.id || "",
            name: datos?.name || "",
            surname: datos?.surname || "",
            email: datos?.email || "",
            dni: datos?.dni || "",
            phone: datos?.phone || "",
            password: "",
            roles: datos?.roles || [],
        },
    });
    const rolesOptions = [
        { label: "Administrador", value: "ROLE_ADMIN" },
        { label: "Cajero/a", value: "ROLE_CASHER" },
        { label: "Gestión Aula", value: "ROLE_ADMIN_VC" },
        { label: "Gestión Usuarios", value: "ROLE_ADMIN_USERS" },
        { label: "Gestión Cursos", value: "ROLE_ADMIN_COURSES" },
        { label: "Padre/Madre", value: "ROLE_PARENT" },
        { label: "Profesor/a", value: "ROLE_TEACHER" },
    ]

    const getUserData = async () => {
        try {
            let datos;
            const res = await fetch({endpoint:`/usuarios/obtener/${user?.id}`,
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${user?.token}`,
                }
            })
            if(res){
                datos = await res.user;
            } else {
                const res = await fetch({endpoint:`/profesores/obtener/${user?.id}`,
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${user?.token}`,
                }})
                if(res){
                    datos = await res.teacher;
                } else {
                    const res = await fetch({endpoint:`/padres/obtener/${user?.id}`,
                    method: "GET",
                    headers: {
                        "Content-Type": "application/json",
                        Authorization: `Bearer ${user?.token}`,
                    }})
                    datos = await res.parent;
                }
            }
            
            form.setValue("id", datos?.id || datos?.id || "")
            form.setValue("name", datos?.name || datos?.name || "")
            form.setValue("surname", datos?.surname || datos?.surname || "")
            form.setValue("email", datos?.email || datos?.email || "")
            form.setValue("dni", datos?.dni || datos?.dni || "")
            form.setValue("phone", datos?.phone || datos?.phone || "")
            form.setValue("password", "")
            form.setValue("roles", datos?.roles || [])
            
            if (res) {
                setActualUserData(datos);
            }
            
        } catch (error) {
            console.error("Error en el useEffect: ", error)
        }
    }

    useEffect(() => {
        if(!actualUserData){
            getUserData();
        }
    },[])

    async function onSubmit(data: z.infer<typeof formSchema>) {
        //if (!user?.token) return;
        const formData = {
            ...actualUserData,
            id: data.id,
            name: data.name,
            surname: data.surname,
            email: data.email,
            dni: data.dni,
            phone: data.phone,
            password: data.password,
            roles: data.roles,
        }
        startLoading()
        try {
            const profileType = actualUserData?.roles.includes("ROLE_PARENT") ? "parent" : actualUserData?.roles.includes("ROLE_TEACHER") ? "teacher" : "user"
            profileType === "parent" && formData.roles
            //const endpoint = isEditable ? `/auth/editar/${datos?.id}?userType=${profileType}` : `/auth/registrar?userType=${profileType}`
            const response = await fetch({endpoint:"/auth/editar?userType=user",
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${user?.token}`,
                },
                formData: {[profileType]: form.getValues()},
            })
            if (response) {
                console.log(response)
                if (typeof mutate === "function") {
                    await mutate(undefined, true)
                }
                toast.success(isEditable ? "Usuario actualizado correctamente." : "Usuario creado correctamente.")
                onClose(false)
                form.reset()
            }
            return response
        } catch (error: any) {
            const errorMessage =
                (typeof error === "object" && error.response ? error.response.data?.message : error?.message) ||
                (isEditable
                    ? "Error al actualizar el usuario. Inténtalo de nuevo."
                    : "Error al crear el usuario. Inténtalo de nuevo.")
            console.error("Error en onSubmit: ", errorMessage)
            toast.error(errorMessage)
        } finally {
            finishLoading()
        }
    }

    return (
        <div className="flex items-center gap-3">
            <AlertDialog open={isEditable ? true : open} onOpenChange={isEditable ? onClose : setOpen}>
                <AlertDialogTrigger asChild>
                    {isEditable ? null : (
                        <Button className="ml-auto" variant="outline">
                            <Plus className="-ms-1 me-2 opacity-60" size={16} strokeWidth={2} aria-hidden="true" />
                            Agregar Usuario
                        </Button>
                    )}
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
                            <AlertDialogTitle>{isEditable ? "Editar Usuario" : "Formulario de inscripción"}</AlertDialogTitle>
                            <AlertDialogDescription>Por favor, complete todos los campos del formulario.</AlertDialogDescription>
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
                            <FormField
                                control={form.control}
                                name="roles"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Rol</FormLabel>

                                        <MultipleSelector
                                                    options={rolesOptions}
                                                    disabled
                                                    //@ts-ignore
                                                    selected={
                                                        field.value?.map(
                                                            (id: string) =>
                                                                ["administrador", "cajero/a", "gestion aula", "gestion usuarios", "gestion cursos"].find(
                                                                    (option) =>
                                                                        option ===
                                                                        id
                                                                ) || {
                                                                    value: id,
                                                                    label: id,
                                                                }
                                                        ) || []
                                                    }
                                                    onChange={(selected) =>
                                                        field.onChange(
                                                            selected.map(
                                                                (option) =>
                                                                    option.value
                                                            )
                                                        )
                                                    }
                                                    placeholder="Seleccionar rol..."
                                                />
                                        {/* <Select onValueChange={field.onChange} defaultValue={field.value[0]}>
                                            <FormControl>
                                                <SelectTrigger>
                                                    <SelectValue placeholder="Seleccione un rol" />
                                                </SelectTrigger>
                                            </FormControl>
                                            <SelectContent>
                                                <SelectItem value="docente">Docente</SelectItem>
                                                <SelectItem value="director">Director</SelectItem>
                                                <SelectItem value="alumno">Alumno</SelectItem>
                                                <SelectItem value="preceptor">Preceptor</SelectItem>
                                            </SelectContent>
                                        </Select> */}
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                        </form>
                    </Form>
                    <AlertDialogFooter>
                        <AlertDialogCancel onClick={() => onClose(false)}>Cancelar</AlertDialogCancel>
                        <AlertDialogAction onClick={form.handleSubmit(onSubmit)}>
                            {loading ? (
                                <Loader2Icon className="animate-spin" size={16} strokeWidth={2} />
                            ) : (
                                <Plus className="-ms-1 me-2 opacity-60" size={16} strokeWidth={2} aria-hidden="true" />
                            )}
                            {loading ? (isEditable ? "Actualizando..." : "Creando...") : isEditable ? "Actualizar" : "Crear"}
                        </AlertDialogAction>
                    </AlertDialogFooter>
                </AlertDialogContent>
            </AlertDialog>
        </div>
    )
}

