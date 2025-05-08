'use client'

import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
    DialogDescription,
    DialogTrigger,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectTrigger, SelectValue, SelectContent, SelectItem } from "@/components/ui/select";
import { Button } from "@/components/ui/button";
import { useState } from "react";

type TipoPago = "PARCIAL" | "TOTAL" | "ACTUALIZAR";

export function ModalPago({ cuota, onSubmit, setPago }: { cuota: any; onSubmit: (data: any, tipoPago:string) => void, setPago: (data: any) => void }) {
    const [open, setOpen] = useState(false);
    const [tipoPago, setTipoPago] = useState<TipoPago>("PARCIAL");
    const [monto, setMonto] = useState("");
    const [porcentaje, setPorcentaje] = useState("");

    const handleApply = () => {
        let pago = {
            id: cuota.id,
            courseId: cuota.curseId,
            studentId: cuota.studentId,
            paymentAmount: cuota.paymentAmount,
            paymentType: "CUOTE",
            paymentDueDate: cuota.paymentDueDate,
            lastPaymentDate: new Date().toISOString(),
        };

        if (tipoPago === "PARCIAL") {
            const paidAmount = cuota.paidAmount + parseFloat(monto);
            const isPaid = paidAmount == cuota.paymentAmount;
            setPago(monto);
            onSubmit({
                ...pago,
                title: cuota.studentName,
                courseId: pago.courseId,
                description: "Pago parcial cuota",
                paymentAmount: cuota.paymentAmount,
                paidAmount,
                isPaid,
                hasDebt: !isPaid,
            }, "PARCIAL");
        }

        if (tipoPago === "TOTAL") {
            onSubmit({
                ...pago,
                title: cuota.studentName,
                description: "Pago cuota",
                paidAmount: cuota.paymentAmount,
                isPaid: true,
                hasDebt: false,
            }, "TOTAL");
        }

        if (tipoPago === "ACTUALIZAR") {
            const porcentajeAplicado = parseFloat(porcentaje);
            const nuevoMonto = cuota.paymentAmount + (cuota.paymentAmount * porcentajeAplicado) / 100;
            //const fechaCorregida = cuota.paymentDueDate.split("T")[0].split("-").reverse().join("-");
            onSubmit({
                ...pago,
                paidAmount: cuota.paidAmount,
                paymentAmount: nuevoMonto,
                //paymentDueDate: fechaCorregida,
            }, "ACTUALIZAR");
        }

        setOpen(false);
    };

    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
                <Button className="w-full">Gestionar Pago</Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-[500px]">
                <DialogHeader>
                    <DialogTitle>Gestión de Pago</DialogTitle>
                    <DialogDescription>
                        Realizar un pago parcial, total o actualizar el valor de la cuota
                    </DialogDescription>
                </DialogHeader>

                <div className="grid gap-4 py-4">
                    <div>
                        <Label>Tipo de operación</Label>
                        <Select value={tipoPago} onValueChange={(val) => setTipoPago(val as TipoPago)}>
                            <SelectTrigger>
                                <SelectValue placeholder="Seleccione tipo de pago" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem value="PARCIAL">Pago Parcial</SelectItem>
                                <SelectItem value="TOTAL">Pago Total</SelectItem>
                                <SelectItem value="ACTUALIZAR">Actualizar Cuota</SelectItem>
                            </SelectContent>
                        </Select>
                    </div>

                    {tipoPago === "PARCIAL" && (
                        <div>
                            <Label>Monto a pagar</Label>
                            <Input
                                type="number"
                                min={0}
                                max={cuota.paymentAmount}
                                value={monto}
                                onChange={(e) => setMonto(e.target.value)}
                                placeholder={`Máximo: $${cuota.paymentAmount}`}
                            />
                        </div>
                    )}

                    {tipoPago === "ACTUALIZAR" && (
                        <div>
                            <Label>Porcentaje a aplicar sobre el costo del curso</Label>
                            <Input
                                type="number"
                                min={1}
                                max={100}
                                value={porcentaje}
                                onChange={(e) => setPorcentaje(e.target.value)}
                                placeholder="Ej: 50 para la mitad del precio"
                            />
                        </div>
                    )}

                    <Button onClick={handleApply}>Aplicar</Button>
                </div>
            </DialogContent>
        </Dialog>
    );
}
