using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace AuraDripBackend.Migrations
{
    /// <inheritdoc />
    public partial class AddControlModeToPlant : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<int>(
                name: "ControlMode",
                table: "Plants",
                type: "int",
                nullable: false,
                defaultValue: 0);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "ControlMode",
                table: "Plants");
        }
    }
}
