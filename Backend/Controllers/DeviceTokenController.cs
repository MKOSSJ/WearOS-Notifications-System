using backend.Services;
using Microsoft.AspNetCore.Mvc;
using backend.Models;

namespace backend.Controllers;
[ApiController]
[Route("api/devicetokens")]
public class DeviceTokenController : ControllerBase
{
    private readonly DeviceTokenService _deviceTokenService;

    public DeviceTokenController(DeviceTokenService deviceTokenService)
    {
        _deviceTokenService = deviceTokenService;
    }

    [HttpGet]
    public async Task<ActionResult<List<DeviceToken>>> GetDeviceTokens()
    {
        var deviceTokens = await _deviceTokenService.GetDeviceTokensAsync();
        return Ok(deviceTokens);
    }

    [HttpGet("{id}")]
    public async Task<ActionResult<DeviceToken>> GetDeviceTokenById(long id)
    {
        var deviceToken = await _deviceTokenService.GetDeviceTokenByIdAsync(id);
        if (deviceToken == null)
        {
            return NotFound();
        }
        return Ok(deviceToken);
    }

    [HttpPost]
    public async Task<ActionResult<DeviceToken>> CreateDeviceToken(DeviceToken deviceToken)
    {
        var createdDeviceToken = await _deviceTokenService.CreateDeviceTokenAsync(deviceToken);
        return CreatedAtAction(nameof(GetDeviceTokenById), new { id = createdDeviceToken.Id }, createdDeviceToken);
    }

    [HttpPut("{id}")]
    public async Task<IActionResult> UpdateDeviceToken(long id, DeviceToken deviceToken)
    {
        if (id != deviceToken.Id)
        {
            return BadRequest();
        }

        var updated = await _deviceTokenService.UpdateDeviceTokenAsync(deviceToken);
        if (!updated)
        {
            return NotFound();
        }

        return NoContent();
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> DeleteDeviceToken(long id)
    {
        var deleted = await _deviceTokenService.DeleteDeviceTokenAsync(id);
        if (!deleted)
        {
            return NotFound();
        }

        return NoContent();
    }
}