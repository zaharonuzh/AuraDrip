using Microsoft.EntityFrameworkCore;
using AuraDripBackend.Data;

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

// --- Ѕлок автоматичного завантаженн€ JSON в базу ---
using (var scope = app.Services.CreateScope())
{
    var services = scope.ServiceProvider;
    var context = services.GetRequiredService<AuraDripBackend.Data.AppDbContext>(); 

    // ѕерев≥р€Їмо, чи таблиц€ порожн€
    if (!context.PlantCatalogs.Any())
    {
        // 1. „итаЇмо текст з файлу
        var jsonText = System.IO.File.ReadAllText("Data/plantscatalog.json");

        // 2. ѕеретворюЇмо JSON-текст на список об'Їкт≥в C#
        var options = new System.Text.Json.JsonSerializerOptions { PropertyNameCaseInsensitive = true };
        var plantsFromJson = System.Text.Json.JsonSerializer.Deserialize<List<AuraDripBackend.Models.PlantCatalog>>(jsonText, options);

        // 3. якщо усп≥шно прочитали - збер≥гаЇмо в базу
        if (plantsFromJson != null)
        {
            context.PlantCatalogs.AddRange(plantsFromJson);
            context.SaveChanges();
            Console.WriteLine("Plant catalog successfully loaded from JSON!");
        }
    }
}

app.Run();
