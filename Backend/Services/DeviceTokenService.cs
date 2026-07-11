using backend.Data;
using backend.Models;
using Microsoft.EntityFrameworkCore;

namespace backend.Services;
public class DeviceTokenService 
{
    private readonly ApplicationDbContext _context;

    public DeviceTokenService(ApplicationDbContext context)
    {
        _context = context;
    }


    public async Task<List<DeviceToken>> GetDeviceTokensAsync()
    {
        return await _context.DeviceTokens.ToListAsync();
    }

    public async Task<DeviceToken?> GetDeviceTokenByIdAsync(long id)
    {
        return await _context.DeviceTokens.FindAsync(id);
    }

    public async Task<DeviceToken> CreateDeviceTokenAsync(DeviceToken deviceToken)
    {
        _context.DeviceTokens.Add(deviceToken);
        await _context.SaveChangesAsync();
        return deviceToken;
    }

    public async Task<bool> UpdateDeviceTokenAsync(DeviceToken deviceToken)
    {
        var existingDeviceToken = await _context.DeviceTokens.FindAsync(deviceToken.Id);
        if (existingDeviceToken == null)
        {
            return false;
        }


        existingDeviceToken.Token = deviceToken.Token;
        existingDeviceToken.Platform = deviceToken.Platform;
        existingDeviceToken.IsActive = deviceToken.IsActive;

        await _context.SaveChangesAsync();
        return true;
    }

    public async Task<bool> DeleteDeviceTokenAsync(long id)
    {
        var deviceToken = await _context.DeviceTokens.FindAsync(id);
        if (deviceToken == null)
        {
            return false;
        }

        _context.DeviceTokens.Remove(deviceToken);
        await _context.SaveChangesAsync();
        return true;
    }
}