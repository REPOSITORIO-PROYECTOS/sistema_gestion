import axios, { AxiosRequestConfig, Method } from "axios";
import { useRouter } from "next/navigation";
// import useStore from "@/context/store";
import { toast } from "sonner";

interface AuthFetchProps {
    endpoint: string;
    redirectRoute?: string;
    formData?: any;
    headers?: any;
    options?: AxiosRequestConfig<any>;
    method?: Method;
}

export function useFetch() {
    const router = useRouter();
    // const { setUser } = useStore((state) => ({
    //     setUser: state.setUser,
    // }));

    const authRouter = async ({
        endpoint,
        formData,
        redirectRoute,
        headers,
        options,
        method = "post", // default method is post
    }: AuthFetchProps) => {
        try {
            const { data } = await axios({
                url: `https://sistema-gestion-1.onrender.com${endpoint}`,
                method,
                headers,
                data: formData,
                ...options,
            });

            if (data.message) {
                toast.success(data.message, {
                    richColors: true,
                });
            }
            // if (data.userLogged) {
            //     setUser(data.userLogged)
            // }
            if (redirectRoute) {
                router.push(redirectRoute);
                router.refresh();
            }
            return data;
        } catch (error: any) {
            console.log(error.response.data.message);
        }
    };

    return authRouter;
}
