using backend.Services;
using Microsoft.AspNetCore.Mvc;
using backend.Models;

namespace backend.Controllers;

[ApiController]
[Route("api/pedidos")]
public class PedidoController : ControllerBase
{
    private readonly PedidoService _pedidoService;
    private readonly NotificacionService _notificacionService;

    public PedidoController(
        PedidoService pedidoService,
        NotificacionService notificacionService
    )
    {
        _pedidoService = pedidoService;
        _notificacionService = notificacionService;
    }

    [HttpGet]
    public async Task<ActionResult<List<Pedido>>> GetPedidos()
    {
        var pedidos = await _pedidoService.GetPedidosAsync();
        return Ok(pedidos);
    }

    [HttpGet("{id}")]
    public async Task<ActionResult<Pedido>> GetPedidoById(long id)
    {
        var pedido = await _pedidoService.GetPedidoByIdAsync(id);

        if (pedido == null)
        {
            return NotFound();
        }

        return Ok(pedido);
    }

   [HttpPost]
public async Task<ActionResult<Pedido>> CreatePedido(Pedido pedido)
{
    Console.WriteLine("POST /api/pedidos recibido");

    var createdPedido = await _pedidoService.CreatePedidoAsync(pedido);

    Console.WriteLine($"Pedido creado con ID: {createdPedido.Id}");
    Console.WriteLine("Enviando notificación...");

    await _notificacionService.SendNotificationToAllAsync(
        "Nueva solicitud de mantenimiento",
        $"Cliente: {createdPedido.Cliente}. {createdPedido.Descripcion}"
    );

    Console.WriteLine("Proceso de notificación terminado");

    return CreatedAtAction(
        nameof(GetPedidoById),
        new { id = createdPedido.Id },
        createdPedido
    );
}

    [HttpPut("{id}")]
    public async Task<IActionResult> UpdatePedido(long id, Pedido pedido)
    {
        if (id != pedido.Id)
        {
            return BadRequest();
        }

        var updated = await _pedidoService.UpdatePedidoAsync(pedido);

        if (!updated)
        {
            return NotFound();
        }

        return NoContent();
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> DeletePedido(long id)
    {
        var deleted = await _pedidoService.DeletePedidoAsync(id);

        if (!deleted)
        {
            return NotFound();
        }

        return NoContent();
    }
}