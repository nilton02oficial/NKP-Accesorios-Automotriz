package com.nkp.accesorios;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.graphics.Color;
import android.text.InputType;
import android.view.Gravity;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {

    LinearLayout principal;
    ArrayList<Producto> productos = new ArrayList<>();
    ArrayList<Venta> ventas = new ArrayList<>();
    android.content.SharedPreferences datos;

    static class Producto {
        String nombre;
        String unidad;
        double compraPack;
        double compraUnit;
        double venta;
        double stock;
        double factor;
        double metrosPorRollo;

        Producto(String n, String u, double cp, double cu, double v, double s,
                 double f, double m) {
            nombre = n;
            unidad = u;
            compraPack = cp;
            compraUnit = cu;
            venta = v;
            stock = s;
            factor = f;
            metrosPorRollo = m;
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
        cargarDatos();
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

        TextView dueño = texto("De: Nilton Mamani Calsina", 16);
        dueño.setGravity(Gravity.CENTER);
        principal.addView(dueño);
        principal.addView(texto("Sistema de ventas e inventario", 18));

        Button venta = boton("NUEVA VENTA");
        Button productosBtn = boton("PRODUCTOS E INVENTARIO");
        Button historial = boton("HISTORIAL DE VENTAS");
        Button caja = boton("CAJA");
        Button info = boton("INFORMACIÓN");

        principal.addView(venta);
        principal.addView(productosBtn);
        principal.addView(historial);
        principal.addView(caja);
        principal.addView(info);

        venta.setOnClickListener(v -> nuevaVenta());
        productosBtn.setOnClickListener(v -> mostrarProductos());
        historial.setOnClickListener(v -> mostrarVentas());
        caja.setOnClickListener(v -> mostrarCaja());
        info.setOnClickListener(v ->
            new AlertDialog.Builder(this)
                .setTitle("NKP Accesorios Automotriz")
                .setMessage("Sistema de ventas e inventario\n\nDe: Nilton Mamani Calsina")
                .setPositiveButton("OK", null)
                .show()
        );
    }

    void mostrarProductos() {
        preparar("PRODUCTOS E INVENTARIO");

        Button agregar = boton("AGREGAR PRODUCTO");
        principal.addView(agregar);

        if (productos.isEmpty()) {
            principal.addView(texto("No hay productos registrados.", 17));
        }

        for (int i = 0; i < productos.size(); i++) {
            Producto p = productos.get(i);

            LinearLayout bloque = new LinearLayout(this);
            bloque.setOrientation(LinearLayout.VERTICAL);

            String stockTxt = formatearNumero(p.stock) + " " + p.unidad + " base";
            String packTxt = "Compra: S/ " + dinero(p.compraPack)
                    + " por " + unidadCompraTexto(p);

            TextView info = texto(
                p.nombre +
                "\nUnidad de venta: " + p.unidad +
                "\nStock: " + stockTxt +
                "\n" + packTxt +
                "\nCosto por " + unidadBaseTexto(p) + ": S/ " + dinero(p.compraUnit) +
                "\nVenta por " + unidadBaseTexto(p) + ": S/ " + dinero(p.venta),
                16
            );
            bloque.addView(info);

            Button editar = boton("EDITAR");
            Button eliminar = boton("ELIMINAR");
            LinearLayout acciones = new LinearLayout(this);
            acciones.setOrientation(LinearLayout.HORIZONTAL);
            acciones.addView(editar, new LinearLayout.LayoutParams(0, -2, 1));
            acciones.addView(eliminar, new LinearLayout.LayoutParams(0, -2, 1));
            bloque.addView(acciones);

            final int indice = i;
            editar.setOnClickListener(v -> editarProducto(indice));
            eliminar.setOnClickListener(v -> confirmarEliminarProducto(indice));

            principal.addView(bloque);
        }

        agregar.setOnClickListener(v -> agregarProducto());
        volver();
    }

    void agregarProducto() {
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);

        EditText nombre = campo("Nombre del producto");

        Spinner unidad = new Spinner(this);
        String[] unidades = {"Unidad", "Docena", "Juego", "Caja", "Metro", "Rollo"};
        unidad.setAdapter(new ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_dropdown_item, unidades));

        EditText cantidadCompra = campo("Cantidad de la compra (ej. 1)");
        EditText precioCompra = campo("Precio de compra del paquete");
        EditText venta = campo("Precio de venta por unidad base");
        EditText metrosRollo = campo("Metros por rollo (solo Rollo)");

        cantidadCompra.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        precioCompra.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        venta.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        metrosRollo.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        form.addView(nombre);
        form.addView(unidad);
        form.addView(cantidadCompra);
        form.addView(precioCompra);
        form.addView(venta);
        form.addView(metrosRollo);

        new AlertDialog.Builder(this)
            .setTitle("Nuevo producto")
            .setView(form)
            .setNegativeButton("CANCELAR", null)
            .setPositiveButton("GUARDAR", (d, w) -> {
                try {
                    String n = nombre.getText().toString().trim();
                    if (n.isEmpty()) {
                        mensaje("Ingresa el nombre del producto.");
                        return;
                    }

                    String u = unidad.getSelectedItem().toString();
                    double cantidad = Double.parseDouble(cantidadCompra.getText().toString());
                    double cp = Double.parseDouble(precioCompra.getText().toString());
                    double v = Double.parseDouble(venta.getText().toString());

                    if (cantidad <= 0 || cp < 0 || v < 0) {
                        mensaje("Revisa los datos ingresados.");
                        return;
                    }

                    double factor = factorUnidad(u);
                    double m = 0;

                    if (u.equals("Caja")) {
                        final EditText unidadesCaja = campo("¿Cuántas unidades contiene 1 caja?");
                        new AlertDialog.Builder(this)
                            .setTitle("Conversión de caja")
                            .setView(unidadesCaja)
                            .setNegativeButton("CANCELAR", null)
                            .setPositiveButton("CONTINUAR", (dd, ww) -> {
                                try {
                                    double fc = Double.parseDouble(unidadesCaja.getText().toString());
                                    if (fc <= 0) {
                                        mensaje("La cantidad debe ser mayor que cero.");
                                        return;
                                    }
                                    guardarProducto(n, u, cantidad, cp, v, fc, 0);
                                } catch (Exception ex) {
                                    mensaje("Cantidad de unidades por caja no válida.");
                                }
                            }).show();
                        return;
                    }

                    if (u.equals("Rollo")) {
                        m = Double.parseDouble(metrosRollo.getText().toString());
                        if (m <= 0) {
                            mensaje("Ingresa los metros que tiene 1 rollo.");
                            return;
                        }
                        factor = m;
                    }

                    guardarProducto(n, u, cantidad, cp, v, factor, m);
                } catch (Exception e) {
                    mensaje("Revisa los datos ingresados.");
                }
            })
            .show();
    }

    void guardarProducto(String n, String u, double cantidad, double cp, double v,
                         double factor, double metros) {
        double stockBase = cantidad * factor;
        double costoUnit = cp / factor;

        productos.add(new Producto(n, u, cp, costoUnit, v, stockBase, factor, metros));
        guardarDatos();
        mostrarProductos();

        String extra = "";
        if (u.equals("Docena")) {
            extra = "\n1 docena = 12 unidades.";
        } else if (u.equals("Rollo")) {
            extra = "\n1 rollo = " + formatearNumero(metros) + " metros.";
        } else if (u.equals("Caja")) {
            extra = "\nLa caja se convirtió según las unidades indicadas.";
        }

        mensaje(
            "Producto guardado.\n\n" +
            "Stock base: " + formatearNumero(stockBase) +
            "\nCosto por unidad base: S/ " + dinero(costoUnit) +
            extra
        );
    }

    void editarProducto(int indice) {
        Producto p = productos.get(indice);

        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);

        EditText nombre = campo("Nombre");
        nombre.setText(p.nombre);

        EditText venta = campo("Precio de venta por unidad base");
        venta.setText(String.valueOf(p.venta));

        EditText stock = campo("Stock base actual");
        stock.setText(String.valueOf(p.stock));

        form.addView(nombre);
        form.addView(venta);
        form.addView(stock);

        new AlertDialog.Builder(this)
            .setTitle("Editar producto")
            .setView(form)
            .setNegativeButton("CANCELAR", null)
            .setPositiveButton("GUARDAR", (d, w) -> {
                try {
                    p.nombre = nombre.getText().toString().trim();
                    p.venta = Double.parseDouble(venta.getText().toString());
                    p.stock = Double.parseDouble(stock.getText().toString());
                    guardarDatos();
                    mostrarProductos();
                } catch (Exception e) {
                    mensaje("Revisa los datos.");
                }
            }).show();
    }

    void confirmarEliminarProducto(int indice) {
        Producto p = productos.get(indice);
        new AlertDialog.Builder(this)
            .setTitle("Eliminar producto")
            .setMessage("¿Eliminar \"" + p.nombre + "\"?")
            .setNegativeButton("CANCELAR", null)
            .setPositiveButton("ELIMINAR", (d, w) -> {
                productos.remove(indice);
                guardarDatos();
                mostrarProductos();
            }).show();
    }

    void nuevaVenta() {
        preparar("NUEVA VENTA");

        EditText cliente = campo("Cliente");
        EditText telefono = campo("Teléfono");
        EditText documento = campo("DNI / RUC");
        EditText producto = campo("Producto");
        EditText cantidad = campo("Cantidad");

        cantidad.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        Spinner pago = new Spinner(this);
        String[] opciones = {"Efectivo", "Yape", "Plin", "Tarjeta", "Fiado"};
        pago.setAdapter(new ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_dropdown_item, opciones));

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
                if (p.nombre.equalsIgnoreCase(producto.getText().toString().trim())) {
                    encontrado = p;
                    break;
                }
            }

            if (encontrado == null) {
                mensaje("Producto no encontrado.");
                return;
            }

            try {
                double q = Double.parseDouble(cantidad.getText().toString());
                if (q <= 0 || q > encontrado.stock) {
                    mensaje("Stock insuficiente.");
                    return;
                }

                double total = encontrado.venta * q;
                encontrado.stock -= q;

                ventas.add(new Venta(
                    cliente.getText().toString(),
                    telefono.getText().toString(),
                    documento.getText().toString(),
                    pago.getSelectedItem().toString(),
                    "Venta registrada",
                    total
                ));

                guardarDatos();
                mensaje("Venta registrada\n\nTotal: S/ " + dinero(total));
            } catch (Exception e) {
                mensaje("Cantidad no válida.");
            }
        });

        volver();
    }

    void mostrarVentas() {
        preparar("HISTORIAL DE VENTAS");

        if (ventas.isEmpty()) {
            principal.addView(texto("No hay ventas registradas.", 17));
        } else {
            for (Venta v : ventas) {
                principal.addView(texto(
                    "Cliente: " + v.cliente +
                    "\nDNI/RUC: " + v.documento +
                    "\nPago: " + v.pago +
                    "\nTotal: S/ " + dinero(v.total),
                    16
                ));
            }
        }
        volver();
    }

    void mostrarCaja() {
        preparar("CAJA");

        double total = 0;
        for (Venta v : ventas) total += v.total;

        principal.addView(texto(
            "Ventas realizadas: " + ventas.size() +
            "\n\nTotal vendido: S/ " + dinero(total),
            19
        ));
        volver();
    }

    double factorUnidad(String u) {
        if (u.equals("Docena")) return 12;
        if (u.equals("Juego")) return 1;
        if (u.equals("Caja")) return 1;
        if (u.equals("Metro")) return 1;
        return 1;
    }

    String unidadBaseTexto(Producto p) {
        if (p.unidad.equals("Rollo") || p.unidad.equals("Metro")) return "metro";
        return "unidad";
    }

    String unidadCompraTexto(Producto p) {
        if (p.unidad.equals("Docena")) return "docena";
        if (p.unidad.equals("Juego")) return "juego";
        if (p.unidad.equals("Caja")) return "caja";
        if (p.unidad.equals("Rollo")) return "rollo";
        if (p.unidad.equals("Metro")) return "metro";
        return "unidad";
    }

    String formatearNumero(double n) {
        if (Math.abs(n - Math.round(n)) < 0.000001) {
            return String.valueOf((long)Math.round(n));
        }
        return String.format(Locale.US, "%.2f", n);
    }

    String dinero(double n) {
        return String.format(Locale.US, "%.2f", n);
    }

    void guardarDatos() {
        try {
            JSONArray jp = new JSONArray();
            for (Producto p : productos) {
                JSONObject o = new JSONObject();
                o.put("nombre", p.nombre);
                o.put("unidad", p.unidad);
                o.put("compraPack", p.compraPack);
                o.put("compraUnit", p.compraUnit);
                o.put("venta", p.venta);
                o.put("stock", p.stock);
                o.put("factor", p.factor);
                o.put("metrosRollo", p.metrosPorRollo);
                jp.put(o);
            }

            JSONArray jv = new JSONArray();
            for (Venta v : ventas) {
                JSONObject o = new JSONObject();
                o.put("cliente", v.cliente);
                o.put("telefono", v.telefono);
                o.put("documento", v.documento);
                o.put("pago", v.pago);
                o.put("fecha", v.fecha);
                o.put("total", v.total);
                jv.put(o);
            }

            datos.edit()
                .putString("productos_json", jp.toString())
                .putString("ventas_json", jv.toString())
                .apply();
        } catch (Exception ignored) {
        }
    }

    void cargarDatos() {
        try {
            String sp = datos.getString("productos_json", "");
            if (!sp.isEmpty()) {
                JSONArray a = new JSONArray(sp);
                for (int i = 0; i < a.length(); i++) {
                    JSONObject o = a.getJSONObject(i);
                    productos.add(new Producto(
                        o.optString("nombre", ""),
                        o.optString("unidad", "Unidad"),
                        o.optDouble("compraPack", 0),
                        o.optDouble("compraUnit", 0),
                        o.optDouble("venta", 0),
                        o.optDouble("stock", 0),
                        o.optDouble("factor", 1),
                        o.optDouble("metrosRollo", 0)
                    ));
                }
            }

            String sv = datos.getString("ventas_json", "");
            if (!sv.isEmpty()) {
                JSONArray a = new JSONArray(sv);
                for (int i = 0; i < a.length(); i++) {
                    JSONObject o = a.getJSONObject(i);
                    ventas.add(new Venta(
                        o.optString("cliente", ""),
                        o.optString("telefono", ""),
                        o.optString("documento", ""),
                        o.optString("pago", ""),
                        o.optString("fecha", ""),
                        o.optDouble("total", 0)
                    ));
                }
            }
        } catch (Exception ignored) {
        }
    }

    void mensaje(String texto) {
        new AlertDialog.Builder(this)
            .setMessage(texto)
            .setPositiveButton("OK", null)
            .show();
    }
}
