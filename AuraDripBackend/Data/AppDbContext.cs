using Microsoft.EntityFrameworkCore;
using AuraDripBackend.Models;

namespace AuraDripBackend.Data 
{
    public class AppDbContext : DbContext
    {
        public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) { }

        //Таблиці
        public DbSet<Plant> Plants { get; set; }
        public DbSet<Telemetry> Telemetries { get; set; }
        public DbSet<WateringEvent> WateringEvents { get; set; }
        public DbSet<DailyReport> DailyReports { get; set; }
        public DbSet<PlantCatalog> PlantCatalogs { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {//можна додати додаткові налаштування
            modelBuilder.Entity<Telemetry>()
            .Property(t => t.AirTemperature)
            .HasColumnType("decimal(5, 2)");
        }
    }
}