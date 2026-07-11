using backend.Data;
using backend.Models;
using FirebaseAdmin.Messaging;
using Microsoft.EntityFrameworkCore;

namespace backend.Services;

public class NotificacionService
{
    private readonly ApplicationDbContext _context;

    public NotificacionService(ApplicationDbContext context)
    {
        _context = context;
    }

    public async Task<List<DeviceToken>> GetDeviceTokensAsync()
    {
        return await _context.DeviceTokens.ToListAsync();
    }

    public async Task SendNotificationToAllAsync(string title, string body)
    {
        Console.WriteLine("Intentando enviar notificación...");

        if (FirebaseAdmin.FirebaseApp.DefaultInstance == null)
        {
            Console.WriteLine("ERROR: Firebase Admin NO está inicializado");
            return;
        }

        var tokens = await _context.DeviceTokens
            .Where(t => t.IsActive && t.Platform.ToLower() == "android")
            .Select(t => t.Token)
            .ToListAsync();

        Console.WriteLine($"Tokens encontrados: {tokens.Count}");

        if (tokens.Count == 0)
        {
            Console.WriteLine("No hay tokens activos para enviar notificación");
            return;
        }

        var message = new MulticastMessage()
        {
            Tokens = tokens,
            Notification = new Notification
            {
                Title = title,
                Body = body
            },
            Android = new AndroidConfig
            {
                Priority = Priority.High,
                Notification = new AndroidNotification
                {
                    ChannelId = "maintenance_channel"
                }
            }
        };

        try
        {
            var result = await FirebaseMessaging.DefaultInstance.SendEachForMulticastAsync(message);

            Console.WriteLine($"Notificaciones enviadas correctamente: {result.SuccessCount}");
            Console.WriteLine($"Notificaciones fallidas: {result.FailureCount}");

            foreach (var response in result.Responses)
            {
                if (!response.IsSuccess)
                {
                    Console.WriteLine($"Error FCM: {response.Exception?.Message}");
                }
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine($"ERROR general al enviar notificación: {ex.Message}");
        }
    }

    public async Task SendNotificationToTokensAsync(List<string> tokens, string title, string body)
    {
        Console.WriteLine("Intentando enviar notificación a tokens específicos...");

        if (FirebaseAdmin.FirebaseApp.DefaultInstance == null)
        {
            Console.WriteLine("ERROR: Firebase Admin NO está inicializado");
            return;
        }

        if (tokens == null || tokens.Count == 0)
        {
            Console.WriteLine("No hay tokens para enviar");
            return;
        }

        var message = new MulticastMessage()
        {
            Tokens = tokens,
            Notification = new Notification
            {
                Title = title,
                Body = body
            },
            Android = new AndroidConfig
            {
                Priority = Priority.High,
                Notification = new AndroidNotification
                {
                    ChannelId = "maintenance_channel"
                }
            }
        };

        try
        {
            var result = await FirebaseMessaging.DefaultInstance.SendEachForMulticastAsync(message);

            Console.WriteLine($"Notificaciones enviadas correctamente: {result.SuccessCount}");
            Console.WriteLine($"Notificaciones fallidas: {result.FailureCount}");

            foreach (var response in result.Responses)
            {
                if (!response.IsSuccess)
                {
                    Console.WriteLine($"Error FCM: {response.Exception?.Message}");
                }
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine($"ERROR general al enviar notificación: {ex.Message}");
        }
    }
}