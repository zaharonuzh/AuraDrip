using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace AuraDripBackend.Migrations
{
    /// <inheritdoc />
    public partial class AddWaterCommand : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<bool>(
                name: "HasPendingWaterCommand",
                table: "Plants",
                type: "bit",
                nullable: false,
                defaultValue: false);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "HasPendingWaterCommand",
                table: "Plants");
        }
    }
}
