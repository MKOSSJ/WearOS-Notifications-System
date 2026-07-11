using backend.Models;
using Microsoft.EntityFrameworkCore;
using backend.Data;

namespace backend.Services;

public class PedidoService
{
    private readonly ApplicationDbContext _context;
    private readonly NotificacionService _notificacionService;

    public PedidoService(ApplicationDbContext context, NotificacionService notificacionService)
    {
        _context = context;
        _notificacionService = notificacionService;
    }

    public async Task<List<Pedido>> GetPedidosAsync()
    {
        return await _context.Pedidos.ToListAsync();
    }

    public async Task<Pedido?> GetPedidoByIdAsync(long id)
    {
        return await _context.Pedidos.FindAsync(id);
    }

    public async Task<Pedido> CreatePedidoAsync(Pedido pedido)
    {
        _context.Pedidos.Add(pedido);
        await _context.SaveChangesAsync();

        // Extract tokens while the DbContext is still valid
        var tokens = await _context.DeviceTokens
            .Where(t => t.IsActive && t.Platform.ToLower() == "android")
            .Select(t => t.Token)
            .ToListAsync();

        // Fire-and-forget notification send (service method does not use DbContext)
        _ = Task.Run(async () =>
        {
            try
            {
                await _notificacionService.SendNotificationToTokensAsync(tokens, "Nuevo pedido", $"Nuevo pedido de {pedido.Cliente}");
            }
            catch
            {
                // swallow
            }
        });

        return pedido;
    }

    public async Task<bool> UpdatePedidoAsync(Pedido pedido)
    {
        var existingPedido = await _context.Pedidos.FindAsync(pedido.Id);
        if (existingPedido == null)
        {
            return false;
        }

        existingPedido.Cliente = pedido.Cliente;
        existingPedido.Descripcion = pedido.Descripcion;
        existingPedido.Estado = pedido.Estado;

        await _context.SaveChangesAsync();
        return true;
    }

    public async Task<bool> DeletePedidoAsync(long id)
    {
        var pedido = await _context.Pedidos.FindAsync(id);
        if (pedido == null)
        {
            return false;
        }

        _context.Pedidos.Remove(pedido);
        await _context.SaveChangesAsync();
        return true;
    }
}
