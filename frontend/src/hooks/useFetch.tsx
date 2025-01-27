import axios, { AxiosRequestConfig, Method } from 'axios'
import { useRouter } from 'next/navigation'
// import useStore from "@/context/store";
import { toast } from 'sonner'


interface AuthFetchProps {
    endpoint: string
    redirectRoute?: string
    formData?: any
    options?: AxiosRequestConfig<any>
    method?: Method
}

export function useFetch() {
    const router = useRouter()
    // const { setUser } = useStore((state) => ({
    //     setUser: state.setUser,
    // }));

    const authRouter = async ({
        endpoint,
        formData,
        redirectRoute,
        options,
        method = 'post' // default method is post
    }: AuthFetchProps) => {
        try {
            const { data } = await axios({
                url: `https://sistema-gestion-bovz.onrender.com/${endpoint}`,
                method,
                data: formData,
                ...options
            })
            console.log(data.message);

            if (data.message) {
                toast.success(data.message, {
                    richColors: true,
                });
            }
            // if (data.userLogged) {
            //     setUser(data.userLogged)
            // }
            if (redirectRoute) {
                router.push(redirectRoute)
                router.refresh();
            }
            return data
        } catch (error: any) {
            console.log(error.response.data.message);
        }
    }

    return authRouter
}