import 'services/auth_service.dart';
import 'views/menu_view.dart';

void main() async {
  final authService = AuthService();

  final menuView = MenuView(authService);
  
  await menuView.afficher();
}