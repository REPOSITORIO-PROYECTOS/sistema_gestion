"use client"

import { useDebt } from "@/context/coutasAdeudadasContext";

function filtrarPorDiasVencidos(cuotas:any, dias:any) {
    const hoy:any = new Date();
  
    return cuotas.filter((cuota:any) => {
      const [dia, mes, anio] = cuota.paymentDueDate.split('-');
      const fechaVencimiento:any = new Date(`${anio}-${mes}-${dia}`);
      const diferenciaEnDias = Math.floor((hoy - fechaVencimiento) / (1000 * 60 * 60 * 24));
      return diferenciaEnDias <= dias;
    }).length;
  }

export default function Estadisticas() {
    const {cuotas} = useDebt();
    const cuotasVencidas5:number = filtrarPorDiasVencidos(cuotas, 5);
    const cuotasVencidas15:number = filtrarPorDiasVencidos(cuotas, 15);
    const cuotasVencidas30:number = filtrarPorDiasVencidos(cuotas, 30);
    const totalCuotasDeuda:number = cuotasVencidas5 + cuotasVencidas15 + cuotasVencidas30;
    
    console.log("CUOTAS",cuotasVencidas5, cuotasVencidas15, cuotasVencidas30, totalCuotasDeuda);
    

    return (
        <section className="container flex justify-around items-center gap-8 mx-auto">
            <div className="w-full flex justify-between items-center h-24 px-6 rounded-md bg-[#0b90d1]" >
                <div className="text-white text-lg font-bold leading-relaxed">Deuda 5 dias </div>
                <div className="text-white text-4xl font-bold leading-relaxed">{cuotasVencidas5}</div>
            </div>
            <div className="w-full flex justify-between items-center h-24 px-6 rounded-md bg-[#0b90d1]" >
                <div className="text-white text-lg font-bold leading-relaxed">Deuda 15 dias </div>
                <div className="text-white text-4xl font-bold leading-relaxed">{cuotasVencidas15}</div>
            </div>
            <div className="w-full flex justify-between items-center h-24 px-6 rounded-md bg-[#0b90d1]" >
                <div className="text-white text-lg font-bold leading-relaxed">Deuda 30 dias </div>
                <div className="text-white text-4xl font-bold leading-relaxed">{cuotasVencidas30}</div>
            </div>
            <div className="w-full flex justify-between items-center h-24 px-6 rounded-md bg-[#0b90d1]" >
                <div className="text-white text-lg font-bold leading-relaxed">Total coutas deudas </div>
                <div className="text-white text-4xl font-bold leading-relaxed">{totalCuotasDeuda}</div>
            </div>
        </section>
    )
}
