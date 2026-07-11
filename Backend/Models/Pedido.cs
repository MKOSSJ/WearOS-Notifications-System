namespace backend.Models;

public class Pedido
{
    public long Id { get; set; }
    public string Cliente { get; set; } = string.Empty;
    public string Direccion { get; set; } = string.Empty;
    public string Descripcion { get; set; } = string.Empty;
    public string Estado { get; set; } = "pendiente";
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
}