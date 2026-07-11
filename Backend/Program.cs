using backend.Data;
using Microsoft.EntityFrameworkCore;
using FirebaseAdmin;
using Google.Apis.Auth.OAuth2;
using backend.Services;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();
builder.Services.AddOpenApi();

builder.Services.AddScoped<DeviceTokenService>();
builder.Services.AddScoped<PedidoService>();
builder.Services.AddScoped<NotificacionService>();

builder.Services.AddDbContext<ApplicationDbContext>(options =>
    options.UseMySql(
        builder.Configuration.GetConnectionString("DefaultConnection"),
        ServerVersion.AutoDetect(
            builder.Configuration.GetConnectionString("DefaultConnection")
        )
    )
);

var firebaseCredPath = Path.Combine(
    builder.Environment.ContentRootPath,
    "Firebase",
    "notificaciones-wearos-firebase-adminsdk-fbsvc-a3b35c2f4f.json"
);

Console.WriteLine($"Ruta Firebase: {firebaseCredPath}");

if (File.Exists(firebaseCredPath))
{
    Console.WriteLine("Archivo Firebase encontrado");

    if (FirebaseApp.DefaultInstance == null)
    {
        FirebaseApp.Create(new AppOptions()
        {
            Credential = GoogleCredential.FromFile(firebaseCredPath)
        });

        Console.WriteLine("Firebase Admin inicializado correctamente");
    }
}
else
{
    Console.WriteLine("ERROR: No se encontró el archivo Firebase Admin SDK");
}

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
}

app.UseAuthorization();

app.MapControllers();

app.Run();