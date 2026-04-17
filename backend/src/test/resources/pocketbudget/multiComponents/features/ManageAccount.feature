# language: fr
Fonctionnalité: Gestion des comptes

  Scénario: Créer un compte bancaire
    Étant donné aucun compte existant
    Quand je crée un compte "Épargne" de type "SAVINGS" avec un solde de 1000.0
    Alors le compte "Épargne" devrait exister avec un solde de 1000.0

  Scénario: Supprimer un compte bancaire
    Étant donné aucun compte existant
    Quand je crée un compte "Courant" de type "CHECKING" avec un solde de 500.0
    Et je supprime le compte créé
    Alors le compte ne devrait plus exister
