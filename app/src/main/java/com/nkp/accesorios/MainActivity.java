package com.nkp.accesorios;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.*;
import java.util.ArrayList;

public class MainActivity extends Activity {

    LinearLayout principal;
    ArrayList<Producto> productos = new ArrayList<>();
    ArrayList<Venta> ventas = new ArrayList<>();

    android.content.SharedPreferences datos;

    static class Producto {
        String nombre;
        double compra, venta;
        int stock;

        Producto(String n, double c, double v, int s) {
            nombre = n;
            compra = c;
            venta = v;
            stock = s;
        }
    }

    static class Venta {
        String cliente, telefono, documento, pago, fecha;
        double total;

        Venta(String c, String t, String d, String p, String f, double x) {
            cliente = c;
            telefono = t;
            documento = d;
            pago = p;
            fecha = f;
            total = x;
        }
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        datos = getSharedPreferences("NKP_DATOS", MODE_PRIVATE);

        mostrarInicio();
    }

    TextView texto(String s, int tam) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(tam);
        t.setTextColor(Color.DKGRAY);
        t.setPadding(20, 20, 20, 20);
        return t;
    }

    Button boton(String s) {
        Button b = new Button(this);
        b.setText(s);
        b.setTextSize(16);
        b.setAllCaps(false);
        return b;
    }

    EditText campo(String s) {
        EditText e = new EditText(this);
        e.setHint(s);
        e.setPadding(15, 10, 15, 10);
        return e;
    }

    void preparar(String titulo) {
        principal = new LinearLayout(this);
        principal.setOrientation(LinearLayout.VERTICAL);
        principal.setPadding(15, 15, 15, 15);

        TextView tituloView = texto(titulo, 23);
        tituloView.setGravity(Gravity.CENTER);
        tituloView.setTextColor(Color.rgb(130, 0, 0));

        principal.addView(tituloView);
        setContentView(principal);
    }

    void volver() {
        Button b = boton("VOLVER");
        b.setOnClickListener(v -> mostrarInicio());
        principal.addView(b);
    }

    void mostrarInicio() {
        preparar("NKP ACCESORIOS AUTOMOTRIZ");

        TextView dueño = texto(
                "De: Nilton Mamani Calsina",
                16
        );
        dueño.setGravity(Gravity.CENTER);

        principal.addView(dueño);
        principal.addView(texto(
                "Sistema de ventas e inventario",
                18
        ));

        Button venta = boton("NUEVA VENTA");
        Button productos = boton("PRODUCTOS E INVENTARIO");
        Button historial = boton("HISTORIAL DE VENTAS");
        Button caja = boton("CAJA");
        Button info = boton("INFORMACIÓN");

        principal.addView(venta);
        principal.addView(productos);
        principal.addView(historial);
        principal.addView(caja);
        principal.addView(info);

        venta.setOnClickListener(v -> nuevaVenta());
        productos.setOnClickListener(v -> mostrarProductos());
        historial.setOnClickListener(v -> mostrarVentas());
        caja.setOnClickListener(v -> mostrarCaja());

        info.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("NKP Accesorios Automotriz")
                        .setMessage(
                                "Sistema de ventas e inventario\n\n" +
                                "De: Nilton Mamani Calsina"
                        )
                        .setPositiveButton("OK", null)
                        .show()
        );
    }

    void mostrarProductos() {
        preparar("PRODUCTOS E INVENTARIO");

        Button agregar = boton("AGREGAR PRODUCTO");
        principal.addView(agregar);

        if (productos.isEmpty()) {
            principal.addView(texto(
                    "No hay productos registrados.",
                    17
            ));
        }

        for (int i = 0; i < productos.size(); i++) {

            Producto p = productos.get(i);

            principal.addView(texto(
                    p.nombre +
                    "\nStock: " + p.stock +
                    "\nCompra: S/ " + p.compra +
                    "\nVenta: S/ " + p.venta,
                    16
            ));
        }

        agregar.setOnClickListener(v -> agregarProducto());

        volver();
    }

    void agregarProducto() {

        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);

        EditText nombre = campo("Nombre");
        EditText compra = campo("Precio de compra");
        EditText venta = campo("Precio de venta");
        EditText stock = campo("Stock");

        compra.setInputType(2);
        venta.setInputType(2);
        stock.setInputType(2);

        form.addView(nombre);
        form.addView(compra);
        form.addView(venta);
        form.addView(stock);

        new AlertDialog.Builder(this)
                .setTitle("Nuevo producto")
                .setView(form)
                .setNegativeButton("CANCELAR", null)
                .setPositiveButton("GUARDAR", (d, w) -> {

                    try {
                        String n = nombre.getText().toString();

                        double c = Double.parseDouble(
                                compra.getText().toString()
                        );

                        double v = Double.parseDouble(
                                venta.getText().toString()
                        );

                        int s = Integer.parseInt(
                                stock.getText().toString()
                        );

                        productos.add(
                                new Producto(n, c, v, s)
                        );

                        mostrarProductos();

                    } catch (Exception e) {

                        new AlertDialog.Builder(this)
                                .setMessage("Revisa los datos ingresados.")
                                .setPositiveButton("OK", null)
                                .show();
                    }
                })
                .show();
    }

    void nuevaVenta() {

        preparar("NUEVA VENTA");

        EditText cliente = campo("Cliente");
        EditText telefono = campo("Teléfono");
        EditText documento = campo("DNI / RUC");
        EditText producto = campo("Producto");
        EditText cantidad = campo("Cantidad");

        cantidad.setInputType(2);

        Spinner pago = new Spinner(this);

        String[] opciones = {
                "Efectivo",
                "Yape",
                "Plin",
                "Tarjeta",
                "Fiado"
        };

        pago.setAdapter(
                new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        opciones
                )
        );

        Button registrar = boton("REGISTRAR VENTA");

        principal.addView(cliente);
        principal.addView(telefono);
        principal.addView(documento);
        principal.addView(producto);
        principal.addView(cantidad);
        principal.addView(pago);
        principal.addView(registrar);

        registrar.setOnClickListener(v -> {

            Producto encontrado = null;

            for (Producto p : productos) {
                if (p.nombre.equalsIgnoreCase(
                        producto.getText().toString()
                )) {
                    encontrado = p;
                    break;
                }
            }

            if (encontrado == null) {
                mensaje("Producto no encontrado.");
                return;
            }

            try {

                int q = Integer.parseInt(
                        cantidad.getText().toString()
                );

                if (q <= 0 || q > encontrado.stock) {
                    mensaje("Stock insuficiente.");
                    return;
                }

                double total = encontrado.venta * q;

                encontrado.stock -= q;

                ventas.add(
                        new Venta(
                                cliente.getText().toString(),
                                telefono.getText().toString(),
                                documento.getText().toString(),
                                pago.getSelectedItem().toString(),
                                "Venta registrada",
                                total
                        )
                );

                mensaje(
                        "Venta registrada\n\nTotal: S/ " +
                        String.format("%.2f", total)
                );

            } catch (Exception e) {
                mensaje("Cantidad no válida.");
            }
        });

        volver();
    }

    void mostrarVentas() {

        preparar("HISTORIAL DE VENTAS");

        if (ventas.isEmpty()) {

            principal.addView(
                    texto("No hay ventas registradas.", 17)
            );

        } else {

            for (Venta v : ventas) {

                principal.addView(
                        texto(
                                "Cliente: " + v.cliente +
                                "\nDNI/RUC: " + v.documento +
                                "\nPago: " + v.pago +
                                "\nTotal: S/ " +
                                String.format("%.2f", v.total),
                                16
                        )
                );
            }
        }

        volver();
    }

    void mostrarCaja() {

        preparar("CAJA");

        double total = 0;

        for (Venta v : ventas) {
            total += v.total;
        }

        principal.addView(
                texto(
                        "Ventas realizadas: " + ventas.size() +
                        "\n\nTotal vendido: S/ " +
                        String.format("%.2f", total),
                        19
                )
        );

        volver();
    }

    void mensaje(String texto) {

        new AlertDialog.Builder(this)
                .setMessage(texto)
                .setPositiveButton("OK", null)
                .show();
    }
}
