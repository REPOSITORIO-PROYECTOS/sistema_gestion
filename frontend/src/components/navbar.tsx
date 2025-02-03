import Link from 'next/link'
import { Button } from "@/components/ui/button"
import { BookOpenIcon, PhoneCall } from 'lucide-react'

export function Navbar() {
    return (
        <nav className="bg-white shadow-md">
            <div className="container mx-auto px-6 py-3 flex justify-between items-center space-x-4">
                <div>
                    <Button className='bg-blue-700 hover:bg-blue-900' asChild>
                        <Link href="/">Home</Link>
                    </Button>
                    <Button variant="ghost" className='hover:bg-blue-700 hover:text-white' asChild>
                        <Link href="/inventario">Inventario</Link>
                    </Button>
                    <Button variant="ghost" className='hover:bg-blue-700 hover:text-white' asChild>
                        <Link href="/cursos">Cursos</Link>
                    </Button>
                    <Button variant="ghost" className='hover:bg-blue-700 hover:text-white' asChild>
                        <Link href="/usuarios">Usuarios</Link>
                    </Button>
                    <Button variant="ghost" className='hover:bg-blue-700 hover:text-white' asChild>
                        <Link href="/historial">Historial</Link>
                    </Button>
                </div>
                <div className="space-x-4">
                    <Button className="bg-blue-600 hover:bg-blue-800">
                        Manual del Usuario
                        <BookOpenIcon className="w-5 h-5 ml-2" aria-hidden="true" />
                    </Button>
                    <Button className="bg-blue-600 hover:bg-blue-800">
                        Soporte
                        <PhoneCall className="w-5 h-5 ml-2" aria-hidden="true" />
                    </Button>
                </div>
            </div>
        </nav>
    )
}

