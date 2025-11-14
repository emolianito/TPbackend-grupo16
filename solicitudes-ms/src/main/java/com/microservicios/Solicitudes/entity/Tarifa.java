package com.microservicios.Solicitudes.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Builder;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tarifa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private LocalDate fechaInicioVigencia;
    private LocalDate fechaFinVigencia;

    private Double costoBasePorKm;               // costo por km estimado
    private Double costoLitroCombustible;        // precio promedio del combustible
    private Double consumoPromedioCombustible;   // consumo combustible promedio
    private Double costoEstadiaDiariaDeposito;   // estadía estimada
    private Double costoFijoPorTramo;               // costo fijo por tramo
}

    /*
      public static double calcularCostoSolicitud(SolicitudTransporte solicitud, Tarifa tarifa, List<Deposito> depositos) {

        double costoTotal = 0.0;

        // ✅ 1. Obtener los tramos de la solicitud
        List<Tramo> tramos = solicitud.getRuta().getTramos();

        for (int i = 0; i < tramos.size(); i++) {
            Tramo tramo = tramos.get(i);

            // ✅ 2. Calcular costo base del tramo
            double costoKm = tramo.getKm() * tarifa.getPrecioKmBase();

            // ✅ 3. Calcular costo por consumo de combustible (si se tiene dato de consumo promedio)
            Camion camion = tramo.getCamion();
            double consumoCombustible = tramo.getKm() * camion.getConsumoPromedioKm();
            double costoCombustible = consumoCombustible * tarifa.getPrecioLitroCombustible();

            // ✅ 4. Sumar costo base por tramo
            double costoTramo = costoKm + costoCombustible + tarifa.getCostoPorTramo();

            costoTotal += costoTramo;

            // ✅ 5. Si no es el último tramo, calcular costo por estadía en depósito intermedio
            if (i < tramos.size() - 1) {
                Tramo tramoSiguiente = tramos.get(i + 1);

                // Se calcula la cantidad de días entre fin de este tramo e inicio del siguiente
                long diasEstadia = ChronoUnit.DAYS.between(tramo.getFechaFin(), tramoSiguiente.getFechaInicio());

                if (diasEstadia > 0) {
                    // Buscar el depósito correspondiente al destino de este tramo
                    Deposito deposito = buscarDepositoPorUbicacion(tramo.getUbicacionDestino(), depositos);

                    if (deposito != null) {
                        double costoEstadia = diasEstadia * deposito.getCostoDiaEstadia();
                        costoTotal += costoEstadia;
                    }
                }
            }
        }

        // ✅ 6. Asignar costo total a la solicitud
        solicitud.setCostoReal(costoTotal);
        return costoTotal;
    }

    private static Deposito buscarDepositoPorUbicacion(Ubicacion ubicacionDestino, List<Deposito> depositos) {
        for (Deposito deposito : depositos) {
            if (deposito.getUbicacion().equals(ubicacionDestino)) {
                return deposito;
            }
        }
        return null;
    }

    SolicitudTransporte solicitud = servicioSolicitudes.obtenerPorId(1);
Tarifa tarifa = servicioTarifas.obtenerVigente();
List<Deposito> depositos = servicioDepositos.obtenerTodos();

double costo = CalculadoraCostos.calcularCostoSolicitud(solicitud, tarifa, depositos);

     */
