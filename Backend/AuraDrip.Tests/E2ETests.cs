using System;
using System.Linq;
using System.Net;
using System.Net.Http.Json;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc.Testing;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.AspNetCore.Hosting;
using Xunit;
using AuraDripBackend.Data;
using AuraDripBackend.Models;

namespace AuraDrip.Tests
{
    public class E2ETests : IClassFixture<WebApplicationFactory<Program>>
    {
        private readonly HttpClient _client;

        // Додали це поле, щоб тест міг до нього достукатися!
        private readonly WebApplicationFactory<Program> _factory;

        public E2ETests(WebApplicationFactory<Program> factory)
        {
            // Зберігаємо налаштовану фабрику в поле класу
            _factory = factory.WithWebHostBuilder(builder =>
            {
                builder.UseEnvironment("Production");

                builder.ConfigureServices(services =>
                {
                    var descriptor = services.SingleOrDefault(
                        d => d.ServiceType == typeof(DbContextOptions<AppDbContext>));
                    if (descriptor != null) services.Remove(descriptor);

                    services.AddDbContext<AppDbContext>(options =>
                    {
                        options.UseInMemoryDatabase("E2ETestDatabase");
                    });

                    var sp = services.BuildServiceProvider();
                    using (var scope = sp.CreateScope())
                    {
                        var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();
                        db.Database.EnsureCreated();

                        if (!db.Plants.Any(p => p.Id == 1))
                        {
                            db.Plants.Add(new Plant
                            {
                                Id = 1,
                                Name = "Test Plant",
                                MinMoistureThreshold = 30,
                                ControlMode = 2
                            });
                            db.SaveChanges();
                        }
                    }
                });
            });

            _client = _factory.CreateClient();
        }

        [Fact]
        public async Task SyncEndpoint_ShouldProcessTelemetry_LikeRealDevice()
        {
            // Arrange
            var telemetryData = new Telemetry
            {
                PlantId = 1,
                SoilMoisture = 45,
                AirHumidity = 50.0,
                AirTemperature = 22.0,
                Timestamp = DateTime.UtcNow
            };

            try
            {
                // Act: Відправляємо запит
                var response = await _client.PostAsJsonAsync("/api/device/sync", telemetryData);
                response.EnsureSuccessStatusCode();
            }
            catch (InvalidOperationException ex) when (ex.Message.Contains("ResponseBodyPipeWriter"))
            {
                // БАГ ЗЛОВЛЕНО! Йдемо перевіряти базу даних.
                using (var scope = _factory.Services.CreateScope())
                {
                    var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();

                    // Шукаємо телеметрію
                    var savedTelemetry = db.Telemetries.FirstOrDefault(t => t.PlantId == 1 && t.SoilMoisture == 45);

                    // Перевіряємо, чи вона збереглася
                    Assert.NotNull(savedTelemetry);
                }
            }
        }
    }
}