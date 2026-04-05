using AuraDripBackend.Data;

using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.

builder.Services.AddControllers();
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseSqlServer(builder.Configuration.GetConnectionString("DefaultConnection")));

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();

app.UseAuthorization();

app.MapControllers();

// --- Блок автоматичного завантаження JSON в базу ---
using (var scope = app.Services.CreateScope())
{
    var context = scope.ServiceProvider.GetRequiredService<AuraDripBackend.Data.AppDbContext>();

    // 1. Формуємо шлях до файлу
    var basePath = AppContext.BaseDirectory;
    var filePath = Path.Combine(basePath, "Data", "plantscatalog.json");

    // 2. Якщо файл є і база порожня - завантажуємо
    if (File.Exists(filePath) && !context.PlantCatalogs.Any())
    {
        var jsonText = File.ReadAllText(filePath);
        var options = new System.Text.Json.JsonSerializerOptions { PropertyNameCaseInsensitive = true };
        var plantsFromJson = System.Text.Json.JsonSerializer.Deserialize<List<AuraDripBackend.Models.PlantCatalog>>(jsonText, options);

        if (plantsFromJson != null && plantsFromJson.Any())
        {
            context.PlantCatalogs.AddRange(plantsFromJson);
            context.SaveChanges();
            Console.WriteLine("Plant catalog successfully loaded from JSON!");
        }
    }
}

app.Run();
public partial class Program { }
//до невидимого класу Program, який компілятор сам створив, просимо додати статус public (публічний),
//щоб мої тести з сусіднього проєкту могли його бачити і запускати